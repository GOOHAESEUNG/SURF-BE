package com.tavemakers.surf.domain.comment.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "댓글 수정 DTO")
public record CommentUpdateReqDTO(
        @Schema(description = "내용", example = "내용 수정합니다.")
        @NotBlank String content
) {}