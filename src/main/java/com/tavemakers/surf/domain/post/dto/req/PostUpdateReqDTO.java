package com.tavemakers.surf.domain.post.dto.req;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public record PostUpdateReqDTO(
        @NotBlank String title,
        @NotBlank String content,
        Boolean pinned,
        LocalDateTime postedAt
) {
}
