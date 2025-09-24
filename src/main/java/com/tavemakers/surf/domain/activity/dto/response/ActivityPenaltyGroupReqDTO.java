package com.tavemakers.surf.domain.activity.dto.response;

import lombok.Builder;

@Builder
public record ActivityPenaltyGroupReqDTO(
        ActivityTypeGroupCountResDTO late,
        ActivityTypeGroupCountResDTO absence
) {
    public static ActivityPenaltyGroupReqDTO of(ActivityTypeGroupCountResDTO late, ActivityTypeGroupCountResDTO absence) {
        return ActivityPenaltyGroupReqDTO.builder()
                .late(late)
                .absence(absence)
                .build();
    }
}
