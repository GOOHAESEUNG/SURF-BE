package com.tavemakers.surf.domain.home.dto.response;

import com.tavemakers.surf.domain.home.entity.HomeBanner;
import lombok.Builder;

@Builder
public record HomeBannerResDTO(
        Long id,
        String imageUrl,
        String linkUrl,
        Integer displayOrder
) {
    public static HomeBannerResDTO from(HomeBanner b) {
        return HomeBannerResDTO.builder()
                .id(b.getId())
                .imageUrl(b.getImageUrl())
                .linkUrl(b.getLinkUrl())
                .displayOrder(b.getDisplayOrder())
                .build();
    }
}