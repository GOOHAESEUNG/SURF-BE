package com.tavemakers.surf.domain.score.usecase;

import com.tavemakers.surf.domain.activity.dto.response.ActivityRecordSummaryResDTO;
import com.tavemakers.surf.domain.activity.entity.ActivityRecord;
import com.tavemakers.surf.domain.activity.mapper.ActivityRecordMapper;
import com.tavemakers.surf.domain.activity.service.ActivityRecordGetService;
import com.tavemakers.surf.domain.score.dto.response.PersonalScoreWithPinned5ResDto;
import com.tavemakers.surf.domain.score.entity.PersonalActivityScore;
import com.tavemakers.surf.domain.score.service.PersonalScoreGetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PersonalScoreUsecase {

    private final PersonalScoreGetService personalScoreGetService;
    private final ActivityRecordGetService activityRecordGetService;
    private final ActivityRecordMapper activityRecordMapper;

    public PersonalScoreWithPinned5ResDto findPersonalScoreAndTop4(Long memberId) {
        // 개인 점수, 고정 5개 활동기록 조회
        PersonalActivityScore personalScore = personalScoreGetService.getPersonalScore(memberId);

        List<ActivityRecord> list = activityRecordGetService.findAllByMemberId(memberId);
        ActivityRecordSummaryResDTO dto = activityRecordMapper.mapper5PinnedActivityRecord(list);

        return PersonalScoreWithPinned5ResDto.of(personalScore.getScore(), dto);
    }

}
