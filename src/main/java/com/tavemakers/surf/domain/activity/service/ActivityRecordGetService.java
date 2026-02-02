package com.tavemakers.surf.domain.activity.service;

import com.tavemakers.surf.domain.activity.entity.ActivityRecord;
import com.tavemakers.surf.domain.activity.entity.enums.ScoreType;
import com.tavemakers.surf.domain.activity.repository.ActivityRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ActivityRecordGetService {

    private final ActivityRecordRepository activityRecordRepository;

    /** 회원의 활동기록 목록을 점수 유형별 페이징 조회 */
    public Slice<ActivityRecord> findActivityRecordList(Long memberId,ScoreType scoreType, Pageable pageable) {
        return activityRecordRepository.findActivityRecordListByMemberId(memberId, scoreType, pageable);
    }

    /** 회원의 전체 활동기록 조회 */
    public List<ActivityRecord> findAllByMemberId(Long memberId) {
        return activityRecordRepository.findByMemberIdAndIsDeleted(memberId, false);
    }

}
