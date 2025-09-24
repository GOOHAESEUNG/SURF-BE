package com.tavemakers.surf.domain.activity.dto.response;

import lombok.Builder;

@Builder
public record ActivityRecordSummaryResDTO(
        ActivityRewardGroupReqDTO rewards,
        ActivityPenaltyGroupReqDTO penalties
) {
    public static ActivityRecordSummaryResDTO of(
            ActivityRewardGroupReqDTO rewards,
            ActivityPenaltyGroupReqDTO penalties
    ) {
        return ActivityRecordSummaryResDTO.builder()
                .rewards(rewards)
                .penalties(penalties)
                .build();
    }

}
