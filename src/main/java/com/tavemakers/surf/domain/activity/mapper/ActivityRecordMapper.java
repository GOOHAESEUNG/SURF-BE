package com.tavemakers.surf.domain.activity.mapper;

import com.tavemakers.surf.domain.activity.dto.response.ActivityRecordSummaryResDTO;
import com.tavemakers.surf.domain.activity.dto.response.ActivityTypeCountResDTO;
import com.tavemakers.surf.domain.activity.entity.ActivityRecord;
import com.tavemakers.surf.domain.activity.entity.enums.ActivityType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ActivityRecordMapper {

    private static final List<ActivityType> SINGLE_RECORDS = List.of(
            ActivityType.UPLOAD_INSTAGRAM_STORY,
            ActivityType.ENGAGE_TECH_SEMINAR,
            ActivityType.EARLY_BIRD
    );

    private static final List<ActivityType> GROUP_RECORDS = List.of(
            ActivityType.WRITE_WIL,
            ActivityType.UPLOAD_TAVE_REVIEW
    );

    public ActivityRecordSummaryResDTO mapper5PinnedActivityRecord(List<ActivityRecord> records) {
        Map<ActivityType, Long> countMap = records.stream()
                .collect(Collectors.groupingBy(ActivityRecord::getActivityType, Collectors.counting()));

        List<ActivityTypeCountResDTO> singleList = SINGLE_RECORDS.stream()
                .map(type -> ActivityTypeCountResDTO.of(type, countMap.getOrDefault(type, 0L)))
                .toList();

        List<ActivityTypeCountResDTO> group = GROUP_RECORDS.stream()
                .map(type -> ActivityTypeCountResDTO.of(type, countMap.getOrDefault(type, 0L)))
                .toList();

        return ActivityRecordSummaryResDTO.of(singleList, group);
    }

}
