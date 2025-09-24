package com.tavemakers.surf.domain.activity.dto.test;

import com.tavemakers.surf.domain.activity.dto.response.ActivityTypeCountResDTO;
import com.tavemakers.surf.domain.activity.dto.response.ActivityTypeGroupCountResDTO;
import lombok.Builder;

import java.util.List;

@Builder
public record ActivityRewardGroupReqDTO(
        List<ActivityTypeCountResDTO> taveActivities,
        ActivityTypeGroupCountResDTO blogs
) {

    public static ActivityRewardGroupReqDTO of(
            List<ActivityTypeCountResDTO> taveActivities,
            ActivityTypeGroupCountResDTO blogs
    ) {
        return ActivityRewardGroupReqDTO.builder()
                .taveActivities(taveActivities)
                .blogs(blogs)
                .build();
    }
}
