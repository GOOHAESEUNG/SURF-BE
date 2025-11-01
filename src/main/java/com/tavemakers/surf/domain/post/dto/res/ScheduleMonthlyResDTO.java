package com.tavemakers.surf.domain.post.dto.res;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.YearMonth;
import java.util.List;

@Schema(description = "월벌 일정 조회")
public record ScheduleMonthlyResDTO(
        @Schema(description = "조회 연월", example = "2025-03")
        YearMonth month,

        List<ScheduleResDTO> scheduleResDTOList
) {
}
