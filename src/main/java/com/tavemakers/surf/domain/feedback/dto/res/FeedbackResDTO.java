package com.tavemakers.surf.domain.feedback.dto.res;

import com.tavemakers.surf.domain.feedback.entity.Feedback;

import java.time.LocalDateTime;

public record FeedbackResDTO(
        Long id,
        String content,
        LocalDateTime createdAt
) {
    public static FeedbackResDTO from(Feedback f) {
        return new FeedbackResDTO(
                f.getId(),
                f.getContent(),
                f.getCreatedAt()
        );
    }
}
