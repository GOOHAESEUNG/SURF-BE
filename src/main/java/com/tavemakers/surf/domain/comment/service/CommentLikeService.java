package com.tavemakers.surf.domain.comment.service;

import com.tavemakers.surf.domain.comment.entity.Comment;
import com.tavemakers.surf.domain.comment.entity.CommentLike;
import com.tavemakers.surf.domain.comment.exception.CommentNotFoundException;
import com.tavemakers.surf.domain.comment.repository.CommentLikeRepository;
import com.tavemakers.surf.domain.comment.repository.CommentRepository;
import com.tavemakers.surf.domain.member.entity.Member;
import com.tavemakers.surf.domain.member.exception.MemberNotFoundException;
import com.tavemakers.surf.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentLikeService {

    private final CommentLikeRepository commentLikeRepository;
    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;

    /** 좋아요 및 좋아요 취소 */
    @Transactional
    public boolean toggleLike(Long commentId, Long memberId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(CommentNotFoundException::new);
        Member member = memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);

        // 좋아요 이미 존재하면 취소
        return commentLikeRepository.findByCommentAndMember(comment, member)
                .map(existingLike -> {
                    commentLikeRepository.delete(existingLike);
                    comment.decreaseLikeCount();
                    return false; // 좋아요 해제됨
                })
                .orElseGet(() -> {
                    commentLikeRepository.save(CommentLike.of(comment, member));
                    comment.increaseLikeCount();
                    return true; // 좋아요 등록됨
                });
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
}
