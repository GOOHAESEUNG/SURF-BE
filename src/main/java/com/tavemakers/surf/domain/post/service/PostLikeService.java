package com.tavemakers.surf.domain.post.service;

import com.tavemakers.surf.domain.member.entity.Member;
import com.tavemakers.surf.domain.member.exception.MemberNotFoundException;
import com.tavemakers.surf.domain.member.repository.MemberRepository;
import com.tavemakers.surf.domain.notification.entity.NotificationType;
import com.tavemakers.surf.domain.notification.service.NotificationCreateService;
import com.tavemakers.surf.domain.post.entity.Post;
import com.tavemakers.surf.domain.post.entity.PostLike;
import com.tavemakers.surf.domain.post.exception.PostNotFoundException;
import com.tavemakers.surf.domain.post.repository.PostLikeRepository;
import com.tavemakers.surf.domain.post.repository.PostRepository;
import com.tavemakers.surf.global.logging.LogEvent;
import com.tavemakers.surf.global.logging.LogParam;
import jakarta.persistence.OptimisticLockException;
import java.util.Map;
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

    private final NotificationCreateService notificationCreateService;

    @Transactional
    @LogEvent(value = "post.like", message = "게시물 좋아요 성공")
    public void like(
            @LogParam("post_id") Long postId,
            @LogParam("user_id") Long memberId) {

        if (postLikeRepository.existsByPostIdAndMemberId(postId, memberId)) {
            return;
        }

        Post post = postRepository.findById(postId)
                .orElseThrow(PostNotFoundException::new);
        Member member = memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);

        try {
            postLikeRepository.save(PostLike.of(post, member));
            createNotificationAtPostLike(member, postId);
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
    @LogEvent(value = "post.unlike", message = "게시물 좋아요 해제 성공")
    public void unlike(
            @LogParam("post_id")
            Long postId,
            @LogParam("user_id")
            Long memberId) {
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

    /** 좋아요 생성시 알림 - 게시글 작성자에게 */
    protected void createNotificationAtPostLike(
            Member member,
            Long postId
    ) {
        Long postOwnerId   = postRepository.findPostOwnerId(postId);

        // 자기 글이면 알림 안 보냄
        if (postOwnerId.equals(member.getId())) {
            return;
        }

        notificationCreateService.createAndSend(
                postOwnerId,
                NotificationType.POST_LIKE,
                Map.of(
                        "actorName", member.getName(),
                        "postId", postId
                )
        );
    }

}