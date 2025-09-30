package com.tavemakers.surf.domain.comment.dto.res;

import com.tavemakers.surf.domain.comment.entity.Comment;
import io.swagger.v3.oas.annotations.media.Schema;


@Schema(description = "댓글 응답 DTO")
public record CommentResDTO(
        Long id,
        Long postId,
        Long parentId,
        Long rootId,
        int depth,
        String content,
        Long memberId,
        String nickname
) {
    public static CommentResDTO from(Comment comment) {
        return new CommentResDTO(
                comment.getId(),
                comment.getPost().getId(),
                comment.getParent() != null ? comment.getParent().getId() : null,
                comment.getParent() == null ? comment.getId() : comment.getRootId(),
                comment.getDepth(),
                comment.getContent(),
                comment.getMember().getId(),
                comment.getMember().getName()
        );
    }
}