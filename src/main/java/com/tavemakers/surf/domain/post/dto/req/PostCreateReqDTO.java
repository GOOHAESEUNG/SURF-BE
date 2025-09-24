package com.tavemakers.surf.domain.post.dto.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record PostCreateReqDTO(
        @NotNull Long boardId,
        @NotBlank String title,
        @NotBlank String content,
        Boolean pinned,
        LocalDateTime postedAt
) {
}
