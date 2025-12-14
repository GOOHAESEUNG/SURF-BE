package com.tavemakers.surf.domain.letter.dto.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import com.tavemakers.surf.domain.letter.entity.Letter;

import java.time.LocalDateTime;

@Getter
@Builder
@Schema(description = "쪽지 조회 응답 DTO")
public class LetterResponse {

    @Schema(description = "쪽지 ID", example = "12")
    private Long letterId;

    @Schema(description = "쪽지 제목", example = "문의드립니다.")
    private String title;

    @Schema(description = "쪽지 본문 내용", example = "안녕하세요, 몇 가지 문의 사항이 있습니다.")
    private String content;

    @Schema(description = "추가 연락 SNS (선택사항)", example = "@instagram_user")
    private String sns;

    @Schema(description = "발신자가 회신받고 싶은 이메일", example = "sender@example.com")
    private String replyEmail;

    @Schema(description = "발신자 memberId", example = "1")
    private Long senderId;

    @Schema(description = "발신자 이름", example = "임동규")
    private String senderName;

    @Schema(description = "수신자 memberId", example = "3")
    private Long receiverId;

    @Schema(description = "수신자 이름", example = "홍길동")
    private String receiverName;

    @Schema(description = "쪽지 작성 시각", example = "2025-12-25T14:12:33")
    private LocalDateTime createdAt;

    public static LetterResponse from(Letter letter) {
        return LetterResponse.builder()
                .letterId(letter.getNoteId())
                .title(letter.getTitle())
                .content(letter.getContent())
                .sns(letter.getSns())
                .replyEmail(letter.getReplyEmail())
                .senderId(letter.getSender().getId())
                .senderName(letter.getSender().getName())
                .receiverId(letter.getReceiver().getId())
                .receiverName(letter.getReceiver().getName())
                .createdAt(letter.getCreatedAt())
                .build();
    }
}
