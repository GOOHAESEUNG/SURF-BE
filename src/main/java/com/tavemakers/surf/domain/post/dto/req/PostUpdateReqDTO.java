package com.tavemakers.surf.domain.post.dto.req;

import com.tavemakers.surf.global.logging.LogPropsProvider;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Schema(description = "게시글 수정 요청 DTO")
public record PostUpdateReqDTO(

        @Schema(description = "게시글 제목", example = "만남의 장 공지사항")
        @NotBlank String title,

        @Schema(description = "게시글 본문 내용", example = "전반기 만남의 장 언제 어디에 진행합니다!")
        @NotBlank String content,

        @Schema(description = "게시글 상단 고정 여부", example = "true")
        Boolean pinned
) implements LogPropsProvider {

        @Override
        public Map<String, Object> buildProps() {
                List<String> changedFields = new ArrayList<>();
                if (title != null && !title.isBlank()) changedFields.add("title");
                if (content != null && !content.isBlank()) changedFields.add("content");
                if (pinned != null) changedFields.add("pinned");

                return Map.of("changed_fields", changedFields);
        }
}