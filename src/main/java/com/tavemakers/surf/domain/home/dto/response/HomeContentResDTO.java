package com.tavemakers.surf.domain.home.dto.response;

import com.tavemakers.surf.domain.home.entity.HomeContent;
import lombok.Builder;


@Builder
public record HomeContentResDTO(
        Long id,
        String mainText
) {
    public static HomeContentResDTO from(HomeContent hc) {
        return HomeContentResDTO.builder()
                .id(hc.getId())
                .mainText(hc.getMainText())
                .build();
    }
}