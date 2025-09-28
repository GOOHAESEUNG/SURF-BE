package com.tavemakers.surf.domain.post.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Schema(description = "게시글 생성 요청 DTO")
public record PostCreateReqDTO(

        @Schema(description = "게시판 ID", example = "1")
        @NotNull Long boardId,

        @Schema(description = "게시글 제목", example = "만남의 장 공지사항")
        @NotBlank String title,

        @Schema(description = "게시글 본문 내용", example = "전반기 만남의 장 언제 어디에 진행합니다!")
        @NotBlank String content,

        @Schema(description = "게시글 상단 고정 여부", example = "true")
        Boolean pinned,

        @Schema(description = "게시글 작성 일시", example = "2023-10-05T14:48:00")
        LocalDateTime postedAt
) {
}
