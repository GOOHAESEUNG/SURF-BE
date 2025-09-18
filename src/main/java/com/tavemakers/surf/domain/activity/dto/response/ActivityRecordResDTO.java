package com.tavemakers.surf.domain.activity.dto.response;

import com.tavemakers.surf.domain.activity.entity.ActivityRecord;
import lombok.Builder;

import java.time.format.DateTimeFormatter;

@Builder
public record ActivityRecordResDTO(
        Long memberId,
        String categoryName,
        String activityName,
        String scoreType,
        String activityDate,
        double prefixSum,
        double appliedScore
) {

    public static ActivityRecordResDTO from(ActivityRecord activityRecord) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy.MM.dd");
        String formattedDate = activityRecord.getActivityDate().format(formatter);

        String category = null;
        String activityName = null;

        // 대주제가 Null인 경우,
        if (activityRecord.getCategory() == null) {
            category = activityRecord.getActivityType().name();
        } else {
            category = activityRecord.getCategory().name();
            activityName = activityRecord.getActivityType().name();
        }

        return ActivityRecordResDTO.builder()
                .memberId(activityRecord.getMemberId())
                .categoryName(category)
                .activityName(activityName)
                .activityDate(formattedDate)
                .scoreType(activityRecord.getScoreType().name())
                .prefixSum(activityRecord.getPrefixSum())
                .appliedScore(activityRecord.getAppliedScore())
                .build();
    }

}
