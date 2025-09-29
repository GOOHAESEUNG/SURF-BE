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
            post.increaseLikeCount();
        } catch (DataIntegrityViolationException e) {
            // 이미 좋아요 되어 있으면 무시(멱등)
        }
    }

    @Transactional
    public void unlike(Long postId, Long memberId) {
        // 실제 삭제가 일어난 경우에만 카운터 감소
        long deleted = postLikeRepository.deleteByPostIdAndMemberId(postId, memberId);
        if (deleted > 0) {
            Post post = postRepository.findById(postId)
                    .orElseThrow(PostNotFoundException::new);
            post.decreaseLikeCount();
        }
    }

    @Transactional(readOnly = true)
    public boolean isLikedByMe(Long postId, Long memberId) {
        return postLikeRepository.existsByPostIdAndMemberId(postId, memberId);
    }
}