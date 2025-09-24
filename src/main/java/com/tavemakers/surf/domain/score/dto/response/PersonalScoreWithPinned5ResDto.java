package com.tavemakers.surf.domain.score.dto.response;

import com.tavemakers.surf.domain.activity.dto.response.ActivityRecordSummaryResDTO;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record PersonalScoreWithPinned5ResDto(
        BigDecimal score,
        ActivityRecordSummaryResDTO records
) {

    public static PersonalScoreWithPinned5ResDto of(BigDecimal score, ActivityRecordSummaryResDTO dtoList) {

        return PersonalScoreWithPinned5ResDto.builder()
                .score(score)
                .records(dtoList)
                .build();
    }

}
