package com.tavemakers.surf.domain.feedback.dto.req;

import jakarta.validation.constraints.NotBlank;

public record FeedbackCreateReqDTO(
        @NotBlank String content
) {
}
