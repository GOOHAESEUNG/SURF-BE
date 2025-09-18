package com.tavemakers.surf.domain.score.usecase;

import com.tavemakers.surf.domain.activity.entity.enums.ActivityType;
import com.tavemakers.surf.domain.activity.service.ActivityRecordGetService;
import com.tavemakers.surf.domain.score.dto.reponse.PersonalScoreWithTop4ResDto;
import com.tavemakers.surf.domain.score.entity.PersonalActivityScore;
import com.tavemakers.surf.domain.score.service.PersonalScoreGetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class PersonalScoreUsecase {

    private final PersonalScoreGetService personalScoreGetService;
    private final ActivityRecordGetService activityRecordGetService;

    public PersonalScoreWithTop4ResDto findPersonalScoreAndTop4(Long memberId) {
        // 개인 점수, TOP4 조회
        PersonalActivityScore personalScore = personalScoreGetService.getPersonalScore(memberId);
        Map<ActivityType, Long> topActivityRecord = activityRecordGetService.findTopActivityRecord(memberId);

        return PersonalScoreWithTop4ResDto.of(personalScore.getScore(), topActivityRecord);
    }

}
