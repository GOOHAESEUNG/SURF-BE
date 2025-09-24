package com.tavemakers.surf.domain.activity.mapper;

import com.tavemakers.surf.domain.activity.dto.response.ActivityRecordSummaryResDTO;
import com.tavemakers.surf.domain.activity.dto.response.ActivityTypeCountResDTO;
import com.tavemakers.surf.domain.activity.dto.response.ActivityTypeGroupCountResDTO;
import com.tavemakers.surf.domain.activity.dto.response.ActivityPenaltyGroupReqDTO;
import com.tavemakers.surf.domain.activity.dto.response.ActivityRewardGroupReqDTO;
import com.tavemakers.surf.domain.activity.entity.ActivityRecord;
import com.tavemakers.surf.domain.activity.entity.enums.ActivityType;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.tavemakers.surf.domain.activity.constants.ActivityGroupConstants.*;
import static com.tavemakers.surf.domain.activity.entity.enums.ActivityType.*;

@Component
public class ActivityRecordMapper {

    public ActivityRecordSummaryResDTO mapPinnedActivityRecord(List<ActivityRecord> records) {
        Map<ActivityType, Long> countMap = records.stream()
                .collect(Collectors.groupingBy(ActivityRecord::getActivityType, Collectors.counting()));

        ActivityRewardGroupReqDTO rewards = makeRewardGroupReqDTO(countMap);
        ActivityPenaltyGroupReqDTO penalties = makePenaltiesGroup(countMap);

        return ActivityRecordSummaryResDTO.of(rewards, penalties);
    }

    /*
    * refactoring
    *
    * TODO 반복되는 코드가 많아서 개선 필요.
    * OOP, Design 패턴을 고려하면 좋을 듯.
    * */

    private ActivityRewardGroupReqDTO makeRewardGroupReqDTO(Map<ActivityType, Long> countMap) {
        List<ActivityTypeCountResDTO> taveActivities = getActivityTypeCountDTO(countMap, TAVE_ACTIVITIES_RECORDS);
        List<ActivityTypeCountResDTO> blogs = getActivityTypeCountDTO(countMap, BLOG_GROUP_RECORDS);
        return ActivityRewardGroupReqDTO.of(taveActivities, ActivityTypeGroupCountResDTO.of(blogs));
    }

    private ActivityPenaltyGroupReqDTO makePenaltiesGroup(Map<ActivityType, Long> countMap) {
        List<ActivityTypeCountResDTO> sessionLateGroup = getActivityTypeCountDTO(countMap, SESSION_LATE_GROUP);
        List<ActivityTypeCountResDTO> teamLateGroup = getActivityTypeCountDTO(countMap, TEAM_LATE_GROUP);
        List<ActivityTypeCountResDTO> lateList = Arrays.asList(
                ActivityTypeCountResDTO.of(SESSION_LATE, sessionLateGroup.size()),
                ActivityTypeCountResDTO.of(TEAM_LATE, teamLateGroup.size())
        );

        List<ActivityTypeCountResDTO> sessionAbsenceGroup = getActivityTypeCountDTO(countMap, SESSION_ABSENCE_GROUP);
        List<ActivityTypeCountResDTO> teamAbsenceGroup = getActivityTypeCountDTO(countMap, TEAM_ABSENCE_GROUP);
        List<ActivityTypeCountResDTO> absenceList = Arrays.asList(
                ActivityTypeCountResDTO.of(SESSION_ABSENCE, sessionAbsenceGroup.size()),
                ActivityTypeCountResDTO.of(TEAM_ABSENCE, teamAbsenceGroup.size())
        );

        return ActivityPenaltyGroupReqDTO.of(
                ActivityTypeGroupCountResDTO.of(lateList),
                ActivityTypeGroupCountResDTO.of(absenceList)
        );
    }

    private List<ActivityTypeCountResDTO> getActivityTypeCountDTO(Map<ActivityType, Long> countMap, List<ActivityType> activityTypes) {
        return activityTypes.stream()
                .map(type -> ActivityTypeCountResDTO.of(type, countMap.getOrDefault(type, 0L)))
                .toList();
    }

}
