package com.tavemakers.surf.domain.comment.service;

import com.tavemakers.surf.domain.comment.dto.req.CommentCreateReqDTO;
import com.tavemakers.surf.domain.comment.dto.res.CommentListResDTO;
import com.tavemakers.surf.domain.comment.dto.res.CommentResDTO;
import com.tavemakers.surf.domain.comment.dto.res.MentionResDTO;
import com.tavemakers.surf.domain.comment.entity.Comment;
import com.tavemakers.surf.domain.comment.exception.*;
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

        // 1) 루트 댓글 (parentId == null)
        if (req.parentId() == null) {

            // 루트 댓글 생성
            Comment comment = Comment.root(post, member, req.content());
            saved = commentRepository.save(comment);
            saved.markAsRoot();

        } else {

            // 2) 대댓글 생성 (parentId != null)
            Comment parent = commentRepository.findById(req.parentId())
                    .orElseThrow(CommentNotFoundException::new);

            // 다른 게시글의 루트 댓글이면 안됨
            if (!parent.getPost().getId().equals(postId))
                throw new CommentNotFoundException();

            // 삭제된 댓글에는 대댓글 불가
            if (parent.isDeleted())
                throw new CannotReplyToDeletedCommentException();

            // 자동 멘션 여부 검사
            boolean isAuto = Boolean.TRUE.equals(req.isAutoMention());

            if (isAuto) {
                // 자동 멘션 → 대댓글
                Comment child = Comment.child(post, member, req.content(), parent);
                saved = commentRepository.save(child);
            } else {
                // 수동 멘션 → 루트 댓글로 생성
                Comment newRoot = Comment.root(post, member, req.content());
                saved = commentRepository.save(newRoot);
                saved.markAsRoot();
            }
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

        if (comment.isDeleted()) {
            throw new AlreadyDeletedCommentException();
        }

        // 본인이 쓴 댓글인지 확인
        if (!comment.getPost().getId().equals(postId) || !comment.getMember().getId().equals(memberId))
            throw new NotMyCommentException();

        Comment parent = null;
        if (comment.getParent() != null) {
            Long parentId = comment.getParent().getId();
            parent = commentRepository.findById(parentId).orElse(null);
        }

        // 자식 존재 여부 (parentId 기반)
        boolean hasChild = commentRepository.existsByParentId(commentId);

        // 1) 자식 존재 → 소프트 삭제
        if (hasChild) {

            commentLikeRepository.deleteAllByComment(comment);
            commentMentionService.deleteAllByComment(comment);

            commentRepository.softDeleteById(commentId);

            Post post = postRepository.findById(postId)
                    .orElseThrow(PostNotFoundException::new);
            post.decreaseCommentCount();

            return;
        }

        // 2) 그 외 = 하드 삭제 (대댓글 포함)
        commentLikeRepository.deleteAllByComment(comment);
        commentMentionService.deleteAllByComment(comment);

        int deleted = commentRepository.deleteByIdAndPostIdAndMemberId(commentId, postId, memberId);
        if (deleted > 0) {
            Post post = postRepository.findById(postId).orElseThrow(PostNotFoundException::new);
            post.decreaseCommentCount();
        }

        // 후손들도 모두 삭제되었을 경우, softdelete된 댓글 완전 삭제
        while (parent != null) {
            if (parent.isDeleted() && !commentRepository.existsByParentId(parent.getId())) {
                Long nextParentId = parent.getParent() != null ? parent.getParent().getId() : null;

                commentRepository.delete(parent);

                parent = (nextParentId != null)
                        ? commentRepository.findById(nextParentId).orElse(null)
                        : null;
            } else {
                break;
            }
        }
    }

    /** 댓글 목록 조회 (Slice) */
    @Transactional(readOnly = true)
    public CommentListResDTO getComments(Long postId, Pageable pageable, Long memberId) {

        // 1) 댓글 Slice 조회
        Slice<Comment> commentSlice =
                commentRepository.findByPostIdOrderByCreatedAtAsc(postId, pageable);

        // 2) 댓글 총 개수 조회
        long totalCount = commentRepository.countByPostIdAndDeletedFalse(postId);

        // 3) 각 댓글 → DTO 변환
        List<CommentResDTO> commentDtoList = commentSlice.getContent().stream()
                .map(comment -> {
                    List<MentionResDTO> mentions = commentMentionService.getMentions(comment.getId());
                    boolean liked = memberId != null && commentLikeService.isLikedByMe(comment.getId(), memberId);
                    return CommentResDTO.from(comment, mentions, liked);
                })
                .toList();

        // 4) CommentListResDTO로 감싸서 반환
        return new CommentListResDTO(
                commentDtoList,
                totalCount,
                commentSlice.hasNext()
        );
    }
}
