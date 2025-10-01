package com.tavemakers.surf.domain.post.service;

import com.tavemakers.surf.domain.member.entity.Member;
import com.tavemakers.surf.domain.member.exception.MemberNotFoundException;
import com.tavemakers.surf.domain.member.repository.MemberRepository;
import com.tavemakers.surf.domain.post.entity.Post;
import com.tavemakers.surf.domain.post.entity.PostLike;
import com.tavemakers.surf.domain.post.exception.PostNotFoundException;
import com.tavemakers.surf.domain.post.repository.PostLikeRepository;
import com.tavemakers.surf.domain.post.repository.PostRepository;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostLikeService {

    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void like(Long postId, Long memberId) {
        if (postLikeRepository.existsByPostIdAndMemberId(postId, memberId)) {
            return;
        }

        Post post = postRepository.findById(postId)
                .orElseThrow(PostNotFoundException::new);
        Member member = memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);

        try {
            postLikeRepository.save(PostLike.of(post, member));
            // 버전 기반 단일 UPDATE (+재시도)
            for (int i = 0; i < 3; i++) {
                Long v = postRepository.findVersionById(postId);
                if (v == null) throw new PostNotFoundException();
                if (postRepository.increaseLikeCount(postId, v) > 0) break;
                if (i == 2) throw new OptimisticLockException("likeCount 증가 충돌");
            }
        } catch (DataIntegrityViolationException e) {
            // 동시요청으로 UK 충돌 → 이미 좋아요 상태 → 멱등 처리
        }
    }

    @Transactional
    public void unlike(Long postId, Long memberId) {
        long deleted = postLikeRepository.deleteByPostIdAndMemberId(postId, memberId);
        if (deleted > 0) {
            Post post = postRepository.findById(postId)
                    .orElseThrow(PostNotFoundException::new);
            // 버전 기반 단일 UPDATE (+재시도)
            for (int i = 0; i < 3; i++) {
                Long v = postRepository.findVersionById(postId);
                if (v == null) throw new PostNotFoundException();
                if (postRepository.decreaseLikeCount(postId, v) > 0) break;
                if (i == 2) throw new OptimisticLockException("likeCount 감소 충돌");
            }
        }
    }

    @Transactional(readOnly = true)
    public boolean isLikedByMe(Long memberId, Long postId) {
        return postLikeRepository.existsByPostIdAndMemberId(postId, memberId);
    }
}