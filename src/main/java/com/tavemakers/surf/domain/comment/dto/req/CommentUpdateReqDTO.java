package com.tavemakers.surf.domain.comment.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

@Schema(description = "댓글 수정 DTO")
public record CommentUpdateReqDTO(

        @Schema(description = "내용", example = "내용 수정합니다.")
        @NotBlank
        @Size(max = 1000, message = "댓글은 1000자 이하만 가능합니다.")
        @NotBlank String content,

        @Schema(description = "새로 멘션할 회원 ID 목록", example = "[2, 5]")
        List<Long> mentionMemberIds

) {}