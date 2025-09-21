package com.tavemakers.surf.domain.activity.service;

import com.tavemakers.surf.domain.activity.entity.ActivityRecord;
import com.tavemakers.surf.domain.activity.repository.ActivityRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ActivityRecordSaveService {

    private final ActivityRecordRepository activityRecordRepository;

    public void saveActivityRecordList(List<ActivityRecord> recordList) {
        activityRecordRepository.saveAll(recordList);
    }
}
