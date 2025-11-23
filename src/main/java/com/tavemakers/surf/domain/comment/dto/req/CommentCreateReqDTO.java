package com.tavemakers.surf.domain.comment.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

@Schema(description = "댓글 생성 DTO")
public record CommentCreateReqDTO(

        @Schema(
                description = """
                루트 댓글을 생성할 때는 null,
                대댓글을 생성할 때는 루트 댓글의 ID를 입력합니다.
                """,
                example = "null"
        )
        Long rootId, // 해당 값 null 이면 루트 댓글, 값 있으면 대댓글

        @Schema(description = "내용", example = "좋은 공지 감사합니다!")
        @NotBlank
        @Size(max = 1000, message = "댓글은 1000자 이하만 가능합니다.")
        String content,

        @Schema(description = "멘션할 회원 ID 목록", example = "[5]")
        List<Long> mentionMemberIds,

        @Schema(
                description = """
                자동 멘션 여부.
                true: 원 댓글을 클릭하여 자동으로 멘션된 경우 (대댓글)
                false: 사용자가 직접 @입력으로 멘션한 경우 (루트 댓글로 취급)
                """,
                example = "false"
        )
        Boolean isAutoMention
) {}



