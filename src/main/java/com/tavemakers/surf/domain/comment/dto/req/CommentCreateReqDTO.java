package com.tavemakers.surf.domain.comment.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "댓글 생성 DTO")
public record CommentCreateReqDTO(
        @Schema(description = "부모 댓글 ID (루트면 null)", example = "null")
        Long parentId,
        @Schema(description = "내용", example = "좋은 공지 감사합니다!")
        @NotBlank String content
) {}