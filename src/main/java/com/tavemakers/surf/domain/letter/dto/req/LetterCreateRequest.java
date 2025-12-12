package com.tavemakers.surf.domain.letter.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "쪽지 생성 요청 DTO")
public class LetterCreateRequest {

    @Schema(description = "수신자 memberId", example = "3")
    private Long receiverId;

    @Schema(description = "쪽지 제목", example = "문의드립니다.")
    private String title;

    @Schema(description = "쪽지 본문 내용", example = "안녕하세요, 몇 가지 질문이 있어 쪽지드립니다.")
    private String content;

    @Schema(description = "추가 연락 SNS (선택사항)", example = "@instagram_user")
    private String sns;

    @Schema(description = "회신 받을 이메일", example = "sender@example.com")
    private String replyEmail;

}
