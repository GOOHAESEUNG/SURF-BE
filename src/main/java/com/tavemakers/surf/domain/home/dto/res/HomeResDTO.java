package com.tavemakers.surf.domain.home.dto.res;

import java.util.List;

public record HomeResDTO(
        String mainText,
        List<HomeBannerResDTO> banners,
        String memberName,
        Integer memberGeneration,
        String memberPart,
        String nextScheduleTitle,
        Long nextScheduleDaysLeft,
        String nextScheduleDdayLabel
) {
}