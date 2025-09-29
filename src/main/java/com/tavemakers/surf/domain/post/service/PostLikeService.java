package com.tavemakers.surf.domain.post.service;

import com.tavemakers.surf.domain.member.entity.Member;
import com.tavemakers.surf.domain.member.exception.MemberNotFoundException;
import com.tavemakers.surf.domain.member.repository.MemberRepository;
import com.tavemakers.surf.domain.post.entity.Post;
import com.tavemakers.surf.domain.post.entity.PostLike;
import com.tavemakers.surf.domain.post.exception.PostNotFoundException;
import com.tavemakers.surf.domain.post.repository.PostLikeRepository;
import com.tavemakers.surf.domain.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostLikeService {

    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public boolean toggleLike(Long postId, Long memberId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(PostNotFoundException::new);
        Member member = memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);

        return postLikeRepository.findByPostIdAndMemberId(postId, memberId)
                .map(existing -> {
                    postLikeRepository.delete(existing);
                    post.decreaseLikeCount();
                    return false;                // 현재 상태: 좋아요 해제
                })
                .orElseGet(() -> {
                    postLikeRepository.save(PostLike.of(post, member));
                    post.increaseLikeCount();
                    return true;                 // 현재 상태: 좋아요 추가
                });
    }

    @Transactional(readOnly = true)
    public boolean isLiked(Long postId, Long memberId) {
        return postLikeRepository.existsByPostIdAndMemberId(postId, memberId);
    }

    @Transactional
    public void like(Long postId, Long memberId) {
        if (isLiked(postId, memberId)) return;
        toggleLike(postId, memberId);
    }

    @Transactional
    public void unlike(Long postId, Long memberId) {
        if (!isLiked(postId, memberId)) return;
        toggleLike(postId, memberId);
    }
}