package com.tavemakers.surf.domain.comment.service;

import com.tavemakers.surf.domain.comment.dto.req.CommentCreateReqDTO;
import com.tavemakers.surf.domain.comment.dto.res.CommentResDTO;
import com.tavemakers.surf.domain.comment.dto.res.MentionResDTO;
import com.tavemakers.surf.domain.comment.entity.Comment;
import com.tavemakers.surf.domain.comment.exception.CommentDepthExceedException;
import com.tavemakers.surf.domain.comment.exception.CommentNotFoundException;
import com.tavemakers.surf.domain.comment.exception.InvalidBlankCommentException;
import com.tavemakers.surf.domain.comment.exception.NotMyCommentException;
import com.tavemakers.surf.domain.comment.repository.CommentLikeRepository;
import com.tavemakers.surf.domain.comment.repository.CommentRepository;
import com.tavemakers.surf.domain.member.entity.Member;
import com.tavemakers.surf.domain.member.exception.MemberNotFoundException;
import com.tavemakers.surf.domain.member.repository.MemberRepository;
import com.tavemakers.surf.domain.post.entity.Post;
import com.tavemakers.surf.domain.post.exception.PostNotFoundException;
import com.tavemakers.surf.domain.post.repository.PostRepository;
import com.tavemakers.surf.global.logging.LogEvent;
import com.tavemakers.surf.global.logging.LogParam;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final CommentMentionService commentMentionService;
    private final CommentLikeService commentLikeService;
    private final CommentLikeRepository commentLikeRepository;

    /** 댓글 작성 */
    @Transactional
    @LogEvent(value = "comment.create", message = "댓글 생성 성공")
    public CommentResDTO createComment(
            @LogParam("post_id") Long postId,
            Long memberId, CommentCreateReqDTO req) {
        Post post = postRepository.findById(postId).orElseThrow(PostNotFoundException::new);
        Member member = memberRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);
        if (req.content() == null || req.content().isEmpty()) throw new InvalidBlankCommentException();

        // 댓글 생성 (루트/대댓글 분기)
        Comment saved;
        if (req.parentId() == null) {
            Comment comment = Comment.root(post, member, req.content());
            saved = commentRepository.save(comment);
            saved.markAsRoot();
        } else {
            Comment parent = commentRepository.findById(req.parentId()).orElseThrow(CommentNotFoundException::new);
            if (!parent.getPost().getId().equals(postId)) throw new CommentNotFoundException();
            if (parent.getDepth() >= 1) throw new CommentDepthExceedException();
            Comment comment = Comment.child(post, member, req.content(), parent);
            saved = commentRepository.save(comment);
        }
        // 멘션 등록
        commentMentionService.createMentions(saved, req.mentionMemberIds());

        // 댓글 수 증가
        post.increaseCommentCount();

        // 응답 DTO (멘션, 좋아요 포함)
        List<MentionResDTO> mentions = commentMentionService.getMentions(saved.getId());
        boolean liked = false; // 새 댓글은 기본적으로 좋아요 없음
        return CommentResDTO.from(saved, mentions, liked);
    }

    /** 댓글 삭제 */
    @Transactional
    @LogEvent(value = "comment.delete", message = "댓글 삭제 성공")
    public void deleteComment(
            @LogParam("post_id") Long postId,
            @LogParam("comment_id") Long commentId,
            Long memberId
    ) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(CommentNotFoundException::new);

        if (!comment.getPost().getId().equals(postId) || !comment.getMember().getId().equals(memberId))
            throw new NotMyCommentException();

        boolean hasChild = commentRepository.existsByParentId(commentId);
        if (hasChild) {
            // 자식 댓글이 있을 경우 soft delete
            if (!comment.isDeleted()) {
                commentLikeRepository.deleteAllByComment(comment);
                commentMentionService.deleteAllByComment(comment);
                comment.softDelete();
            }
        }

        commentLikeRepository.deleteAllByComment(comment);
        commentMentionService.deleteAllByComment(comment);

        // 자식이 없을 경우 완전 삭제
        int deleted = commentRepository.deleteByIdAndPostIdAndMemberId(commentId, postId, memberId);
        if (deleted > 0) {
            commentMentionService.deleteAllByComment(comment);
            commentLikeRepository.deleteAllByComment(comment);

            Post post = postRepository.findById(postId).orElseThrow(PostNotFoundException::new);
            post.decreaseCommentCount();
        }
    }

    /** 댓글 목록 조회 (Slice) */
    @Transactional(readOnly = true)
    public Slice<CommentResDTO> getComments(Long postId, Pageable pageable, Long memberId) {
        Slice<Comment> commentSlice =
                commentRepository.findByPostIdOrderByCreatedAtAsc(postId, pageable);

        return commentSlice.map(comment -> {
            List<MentionResDTO> mentions = commentMentionService.getMentions(comment.getId());
            boolean liked = memberId != null && commentLikeService.isLikedByMe(comment.getId(), memberId);
            return CommentResDTO.from(comment, mentions, liked);
        });
    }
}