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

}
