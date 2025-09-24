package com.tavemakers.surf.domain.activity.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record ActivityRecordSummaryResDTO(
        List<ActivityTypeCountResDTO> singleList,
        ActivityTypeGroupCountResDTO group
) {
    public static ActivityRecordSummaryResDTO of(
            List<ActivityTypeCountResDTO> singleList,
            List<ActivityTypeCountResDTO> group
    ) {
        return ActivityRecordSummaryResDTO.builder()
                .singleList(singleList)
                .group(ActivityTypeGroupCountResDTO.of(group))
                .build();
    }

}
