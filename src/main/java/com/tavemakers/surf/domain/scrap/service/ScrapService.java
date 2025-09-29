package com.tavemakers.surf.domain.scrap.service;

import com.tavemakers.surf.domain.member.entity.Member;
import com.tavemakers.surf.domain.member.exception.MemberNotFoundException;
import com.tavemakers.surf.domain.member.repository.MemberRepository;
import com.tavemakers.surf.domain.post.dto.res.PostResDTO;
import com.tavemakers.surf.domain.post.entity.Post;
import com.tavemakers.surf.domain.post.exception.PostNotFoundException;
import com.tavemakers.surf.domain.post.repository.PostLikeRepository;
import com.tavemakers.surf.domain.post.repository.PostRepository;
import com.tavemakers.surf.domain.post.service.PostLikeService;
import com.tavemakers.surf.domain.scrap.entity.Scrap;
import com.tavemakers.surf.domain.scrap.repository.ScrapRepository;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScrapService {

    private final ScrapRepository scrapRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final PostLikeRepository postLikeRepository;

    @Transactional
    public void addScrap(Long memberId, Long postId) {
        Member member = memberRepository.findById(memberId).
                orElseThrow(MemberNotFoundException::new);
        Post post = postRepository.findById(postId).
                orElseThrow(PostNotFoundException::new);
        try {
            scrapRepository.save(Scrap.of(member, post));
            // 버전 기반 단일 UPDATE (+재시도)
            for (int i = 0; i < 3; i++) {
                Long v = postRepository.findVersionById(postId);
                if (v == null) throw new PostNotFoundException();
                if (postRepository.increaseScrapCount(postId, v) > 0) break;
                if (i == 2) throw new OptimisticLockException("scrapCount 증가 충돌");
            }
        } catch (DataIntegrityViolationException e) {
            // 이미 스크랩되어 있으면 무시(멱등)
        }
    }

    @Transactional
    public void removeScrap(Long memberId, Long postId) {
        int deleted = scrapRepository.deleteByMemberIdAndPostId(memberId, postId);
        if (deleted > 0) {
            for (int i = 0; i < 3; i++) {
                Long v = postRepository.findVersionById(postId);
                if (v == null) throw new PostNotFoundException();
                if (postRepository.decreaseScrapCount(postId, v) > 0) break;
                if (i == 2) throw new OptimisticLockException("scrapCount 감소 충돌");
            }
        }
    }

    public Page<PostResDTO> getMyScraps(Long memberId, Pageable pageable) {
        Page<Post> page = scrapRepository.findPostsByMemberId(memberId, pageable);

        List<Long> postIds = page.getContent().stream()
                .map(Post::getId)
                .toList();

        Set<Long> likedIds = new HashSet<>(postLikeRepository.findLikedPostIdsByMemberAndPostIds(memberId, postIds));

        return page.map(post ->
                PostResDTO.from(post, true, likedIds.contains(post.getId()))
        );
    }

    public boolean isScrappedByMe(Long memberId, Long postId) {
        return scrapRepository.existsByMemberIdAndPostId(memberId, postId);
    }
}