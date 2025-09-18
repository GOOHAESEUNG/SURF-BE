package com.tavemakers.surf.domain.activity.usecase;

import com.tavemakers.surf.domain.activity.dto.ActivityRecordReqDTO;
import com.tavemakers.surf.domain.activity.entity.ActivityRecord;
import com.tavemakers.surf.domain.activity.entity.enums.ActivityType;
import com.tavemakers.surf.domain.activity.service.ActivityRecordSaveService;
import com.tavemakers.surf.domain.score.entity.PersonalActivityScore;
import com.tavemakers.surf.domain.score.service.PersonalScoreGetService;
import com.tavemakers.surf.domain.score.utils.ScoreCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ActivityRecordUsecase {

    private final ActivityRecordSaveService activityRecordSaveService;
    private final PersonalScoreGetService personalScoreGetService;
    private final ScoreCalculator scoreCalculator;

    @Transactional
    public void createActivityRecordList(ActivityRecordReqDTO dto) {
        List<PersonalActivityScore> scoreList = personalScoreGetService.getPersonalScoreList(dto.memberIdList());

        ActivityType type = ActivityType.valueOf(dto.activityType());
        List<ActivityRecord> recordList = scoreList.stream()
                .map(personalScore -> {
                            double prefixSum = scoreCalculator.calculateScore(personalScore, type.getDelta());
                            return ActivityRecord.of(personalScore.getMember().getId(), dto, prefixSum);
                        }
                ).toList();

        activityRecordSaveService.saveActivityRecordList(recordList);
    }

}
