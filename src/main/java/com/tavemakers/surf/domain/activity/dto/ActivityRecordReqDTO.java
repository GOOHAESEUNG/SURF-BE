package com.tavemakers.surf.domain.activity.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public record ActivityRecordReqDTO(
        @NotNull List<Long> memberIdList,
        String category, // MVP 1에서는 미정.
        @NotNull String activityType,
        @NotNull LocalDate activityDate
) {
}