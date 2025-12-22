package com.tavemakers.surf.domain.comment.service;

import com.tavemakers.surf.domain.comment.dto.res.CommentLikeMemberResDTO;
import com.tavemakers.surf.domain.comment.entity.Comment;
import com.tavemakers.surf.domain.comment.entity.CommentLike;
import com.tavemakers.surf.domain.comment.exception.CannotLikeDeletedCommentException;
import com.tavemakers.surf.domain.comment.exception.CommentNotFoundException;
import com.tavemakers.surf.domain.comment.repository.CommentLikeRepository;
import com.tavemakers.surf.domain.comment.repository.CommentRepository;
import com.tavemakers.surf.domain.member.entity.Member;
import com.tavemakers.surf.domain.member.exception.MemberNotFoundException;
import com.tavemakers.surf.domain.member.repository.MemberRepository;
import com.tavemakers.surf.domain.notification.entity.NotificationType;
import com.tavemakers.surf.domain.notification.service.NotificationCreateService;
import com.tavemakers.surf.domain.post.entity.Post;
import com.tavemakers.surf.domain.post.repository.PostRepository;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentLikeService {

    private final CommentLikeRepository commentLikeRepository;
    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;

    private final NotificationCreateService notificationCreateService;

    /** 좋아요 및 좋아요 취소 */
    @Transactional
    public boolean toggleLike(Long commentId, Long memberId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(CommentNotFoundException::new);
        Member member = memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);

        // soft delete 댓글에는 좋아요 불가
        if (comment.isDeleted()) {
            throw new CannotLikeDeletedCommentException();
        }

        Post post = comment.getPost();
        if (post == null) throw new CommentNotFoundException();

        // 좋아요 이미 존재하면 취소
        int removed = commentLikeRepository.deleteByCommentAndMember(comment, member);
        if (removed > 0) {
            comment.decreaseLikeCount();
            commentRepository.save(comment);
            return false; // 이미 눌러져 있었던 거라서 좋아요 취소됨
        }

        try {
            commentLikeRepository.save(CommentLike.of(comment, member));
            comment.increaseLikeCount();
            commentRepository.save(comment);
            createNotificationAtCommentLike(member, commentId, post.getBoard().getId(), post.getId());

            return true; // 새로 좋아요 등록
        } catch (DataIntegrityViolationException e) {
            return true; // 이미 저장되어 있던 상태 (중복 insert 방어)
        }
    }

    /** 댓글의 총 좋아요 수 */
    @Transactional(readOnly = true)
    public long countLikes(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(CommentNotFoundException::new);
        return commentLikeRepository.countByComment(comment);
    }

    /** 내가 해당 댓글에 좋아요 눌렀는지 여부 */
    @Transactional(readOnly = true)
    public boolean isLikedByMe(Long commentId, Long memberId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(CommentNotFoundException::new);
        Member member = memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);
        return commentLikeRepository.existsByCommentAndMember(comment, member);
    }

    /** 특정 댓글에 좋아요를 누른 회원들의 ID, 이름, 프로필 이미지를 조회 */
    @Transactional(readOnly = true)
    public List<CommentLikeMemberResDTO> getMembersWhoLiked(Long commentId) {
        List<Member> members = commentLikeRepository.findMembersWhoLiked(commentId);
        return members.stream()
                .map(member -> new CommentLikeMemberResDTO(
                        member.getId(),
                        member.getName(),
                        member.getProfileImageUrl()))
                .toList();
    }

    /** 좋아요 생성시 알림 - 댓글 작성자에게 */
    protected void createNotificationAtCommentLike(
            Member member,
            Long commentId,
            Long boardId,
            Long postId
    ) {
        Long commentOwnerId = commentRepository.findCommentOwnerId(commentId);

        if (commentOwnerId == null) {
            return;
        }

        // 자기 글이면 알림 안 보냄
        if (commentOwnerId.equals(member.getId())) {
            return;
        }

        notificationCreateService.createAndSend(
                commentOwnerId,
                NotificationType.COMMENT_LIKE,
                Map.of(
                        "actorName", member.getName(),
                        "boardId", boardId,
                        "postId", postId
                )
        );
    }

}
