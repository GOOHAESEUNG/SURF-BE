package com.tavemakers.surf.domain.activity.dto.request;

import com.tavemakers.surf.domain.activity.entity.enums.ActivityType;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public record ActivityRecordReqDTO(
        @NotNull List<Long> memberIdList,
        String category, // MVP 1에서는 미정.
        @NotNull ActivityType activityName,
        @NotNull LocalDate activityDate
) {
}