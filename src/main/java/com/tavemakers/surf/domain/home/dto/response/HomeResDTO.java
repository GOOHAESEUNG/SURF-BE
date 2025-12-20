package com.tavemakers.surf.domain.home.dto.response;

import java.time.LocalDate;
import java.util.List;

public record HomeResDTO(
        String mainText,
        List<HomeBannerResDTO> banners,
        String memberName,
        Integer memberGeneration,
        String memberPart,
        String nextScheduleTitle,
        LocalDate nextScheduleDate
) {
}