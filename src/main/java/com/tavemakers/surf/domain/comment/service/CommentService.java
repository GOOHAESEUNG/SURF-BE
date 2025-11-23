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

        if (req.rootId() == null) {
            // 루트 댓글 생성
            Comment comment = Comment.root(post, member, req.content());
            saved = commentRepository.save(comment);
            saved.markAsRoot();
        } else {
            // 대댓글 생성
            Comment root = commentRepository.findById(req.rootId())
                    .orElseThrow(CommentNotFoundException::new);

            // 다른 게시글의 루트 댓글이면 안됨
            if (!root.getPost().getId().equals(postId)) throw new CommentNotFoundException();

            // 삭제된 댓글에는 대댓글 불가
            if (root.isDeleted()) throw new CannotReplyToDeletedCommentException();

            // depth=1만 허용이므로 root는 무조건 depth=0이어야 함
            if (root.getDepth() >= 1) throw new CommentDepthExceedException();

            // 자동 멘션 여부 검사
            boolean isAuto = Boolean.TRUE.equals(req.isAutoMention());

            if (isAuto) {
                // depth = 1
                Comment child = Comment.child(post, member, req.content(), root);
                saved = commentRepository.save(child);
            } else {
                // depth = 0 (루트 댓글 취급)
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

        // 본인이 쓴 댓글인지 확인
        if (!comment.getPost().getId().equals(postId) || !comment.getMember().getId().equals(memberId))
            throw new NotMyCommentException();

        boolean isRoot = comment.getDepth() == 0;

        // 자식 댓글 존재 여부 (depth = 1 자식만 체크)
        boolean hasChild = commentRepository.existsByRootIdAndDepth(commentId, 1);

        // 1) 루트 + 자식 존재 → 소프트 삭제
        if (isRoot && hasChild) {

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
