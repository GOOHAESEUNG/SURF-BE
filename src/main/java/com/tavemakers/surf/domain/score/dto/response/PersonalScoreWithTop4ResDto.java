package com.tavemakers.surf.domain.score.dto.response;

import com.tavemakers.surf.domain.activity.dto.response.ActivityTypeCountResDTO;
import com.tavemakers.surf.domain.activity.entity.enums.ActivityType;
import lombok.Builder;

import java.util.List;
import java.util.Map;

@Builder
public record PersonalScoreWithTop4ResDto(
        double score,
        List<ActivityTypeCountResDTO> top4
) {

    public static PersonalScoreWithTop4ResDto of(double score, Map<ActivityType, Long> countMap) {
        List<ActivityTypeCountResDTO> dataList = countMap.entrySet().stream()
                .map(entry -> ActivityTypeCountResDTO.of(entry.getKey(), entry.getValue()))
                .toList();

        return PersonalScoreWithTop4ResDto.builder()
                .score(score)
                .top4(dataList)
                .build();
    }

}
