package com.tavemakers.surf.domain.scrap.service;

import com.tavemakers.surf.domain.member.entity.Member;
import com.tavemakers.surf.domain.member.exception.MemberNotFoundException;
import com.tavemakers.surf.domain.member.repository.MemberRepository;
import com.tavemakers.surf.domain.post.dto.res.PostResDTO;
import com.tavemakers.surf.domain.post.entity.Post;
import com.tavemakers.surf.domain.post.exception.PostNotFoundException;
import com.tavemakers.surf.domain.post.repository.PostRepository;
import com.tavemakers.surf.domain.post.service.PostService;
import com.tavemakers.surf.domain.scrap.entity.Scrap;
import com.tavemakers.surf.domain.scrap.exception.ScrapNotFoundException;
import com.tavemakers.surf.domain.scrap.repository.ScrapRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScrapService {

    private final ScrapRepository scrapRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void addScrap(Long memberId, Long postId) {
        if (scrapRepository.existsByMemberIdAndPostId(memberId, postId)) {
            return;
        }
        Member member = memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);
        Post post = postRepository.findById(postId)
                .orElseThrow(PostNotFoundException::new);

        scrapRepository.save(Scrap.of(member, post));
        post.increaseScrapCount();
    }

    @Transactional
    public void removeScrap(Long memberId, Long postId) {
        Scrap scrap = scrapRepository.findByMemberIdAndPostId(memberId, postId)
                .orElseThrow(ScrapNotFoundException::new);

        scrapRepository.delete(scrap);
        scrap.getPost().decreaseScrapCount();
    }

    public Page<PostResDTO> getMyScraps(Long memberId, Pageable pageable) {
        Page<Post> page = scrapRepository.findPostsByMemberId(memberId, pageable);
        return page.map(post -> PostResDTO.from(post, true));
    }

    public boolean isScrappedByMe(Long memberId, Long postId) {
        return scrapRepository.existsByMemberIdAndPostId(memberId, postId);
    }
}