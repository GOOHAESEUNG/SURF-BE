package com.tavemakers.surf.domain.activity.service;

import com.tavemakers.surf.domain.activity.entity.ActivityRecord;
import com.tavemakers.surf.domain.activity.entity.enums.ActivityType;
import com.tavemakers.surf.domain.activity.entity.enums.ScoreType;
import com.tavemakers.surf.domain.activity.repository.ActivityRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ActivityRecordGetService {

    private final ActivityRecordRepository activityRecordRepository;

    public Slice<ActivityRecord> findActivityRecordList(Long memberId,ScoreType scoreType, Pageable pageable) {
        return activityRecordRepository.findActivityRecordListByMemberId(memberId, scoreType, pageable);
    }

    public Map<ActivityType, Long> findTopActivityRecord(Long memberId) {
        List<ActivityRecord> activityRecordList = activityRecordRepository.findByMemberIdAndIsDeleted(memberId, false);
        return countTop4ActivityRecord(activityRecordList);
    }

    private Map<ActivityType, Long> countTop4ActivityRecord(List<ActivityRecord> activityRecordList) {
        return activityRecordList.stream()
                .collect(Collectors.groupingBy(
                        ActivityRecord::getActivityType,
                        Collectors.counting()
                ))
                .entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())) // 개수 내림차순
                .limit(4)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

}
