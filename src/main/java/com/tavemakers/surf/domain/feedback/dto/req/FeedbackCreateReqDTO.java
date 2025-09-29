package com.tavemakers.surf.domain.feedback.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "피드백 생성 요청 DTO")
public record FeedbackCreateReqDTO(

        @Schema(description = "피드백 본문 내용", example = "열심히 일해주세요 ㅜ.ㅜ")
        @NotBlank String content
) {
}
