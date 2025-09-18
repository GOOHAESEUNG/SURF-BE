package com.tavemakers.surf.domain.activity.usecase;

import com.tavemakers.surf.domain.activity.dto.reqeust.ActivityRecordReqDTO;
import com.tavemakers.surf.domain.activity.dto.response.ActivityRecordResDTO;
import com.tavemakers.surf.domain.activity.dto.response.ActivityRecordSliceResDTO;
import com.tavemakers.surf.domain.activity.entity.ActivityRecord;
import com.tavemakers.surf.domain.activity.entity.enums.ActivityType;
import com.tavemakers.surf.domain.activity.entity.enums.ScoreType;
import com.tavemakers.surf.domain.activity.service.ActivityRecordGetService;
import com.tavemakers.surf.domain.activity.service.ActivityRecordSaveService;
import com.tavemakers.surf.domain.score.entity.PersonalActivityScore;
import com.tavemakers.surf.domain.score.service.PersonalScoreGetService;
import com.tavemakers.surf.domain.score.utils.ScoreCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ActivityRecordUsecase {

    private final ActivityRecordSaveService activityRecordSaveService;
    private final ActivityRecordGetService activityRecordGetService;
    private final PersonalScoreGetService personalScoreGetService;
    private final ScoreCalculator scoreCalculator;

    @Transactional
    public void createActivityRecordList(ActivityRecordReqDTO dto) {
        // 다수의 활동 점수 -> 감점 + 가점 -> 누적합과 함께 활동기록 생성
        List<PersonalActivityScore> scoreList = personalScoreGetService.getPersonalScoreList(dto.memberIdList());
        ActivityType type = ActivityType.valueOf(dto.activityName());
        List<ActivityRecord> recordList = scoreList.stream()
                .map(personalScore -> {
                            double prefixSum = scoreCalculator.calculateScore(personalScore, type.getDelta());
                            return ActivityRecord.of(personalScore.getMember().getId(), dto, prefixSum);
                        }
                ).toList();

        activityRecordSaveService.saveActivityRecordList(recordList);
    }

    public ActivityRecordSliceResDTO getActivityRecordList(Long memberId, ScoreType scoreType, int pageSize, int pageNum) {
        Pageable pageable = PageRequest.of(pageNum, pageSize, Sort.by(Sort.Direction.DESC, "createdAt"));
        Slice<ActivityRecord> slice = activityRecordGetService.findActivityRecordList(memberId, scoreType, pageable);

        return ActivityRecordSliceResDTO.from(slice.map(ActivityRecordResDTO::from));
    }

}
