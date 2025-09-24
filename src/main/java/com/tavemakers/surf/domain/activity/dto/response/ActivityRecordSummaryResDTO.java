package com.tavemakers.surf.domain.activity.dto.response;

import com.tavemakers.surf.domain.activity.dto.test.ActivityPenaltyGroupReqDTO;
import com.tavemakers.surf.domain.activity.dto.test.ActivityRewardGroupReqDTO;
import lombok.Builder;

import java.util.List;

@Builder
public record ActivityRecordSummaryResDTO(
        ActivityRewardGroupReqDTO rewards,
        ActivityPenaltyGroupReqDTO penalties
//        List<ActivityTypeCountResDTO> singleList,
//        ActivityTypeGroupCountResDTO blogs
) {
    public static ActivityRecordSummaryResDTO of(
//            List<ActivityTypeCountResDTO> singleList,
//            List<ActivityTypeCountResDTO> blogs,
            ActivityRewardGroupReqDTO rewards,
            ActivityPenaltyGroupReqDTO penalties
    ) {



        return ActivityRecordSummaryResDTO.builder()
                .rewards(rewards)
                .penalties(penalties)
//                .singleList(singleList)
//                .blogs(ActivityTypeGroupCountResDTO.of(group))
                .build();
    }

}
