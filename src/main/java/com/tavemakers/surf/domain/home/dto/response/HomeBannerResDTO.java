package com.tavemakers.surf.domain.home.dto.response;

import com.tavemakers.surf.domain.home.entity.HomeBanner;

public record HomeBannerResDTO(
        Long id,
        String imageUrl,
        String linkUrl,
        Integer sortOrder
) {
    public static HomeBannerResDTO from(HomeBanner b) {
        return new HomeBannerResDTO(b.getId(), b.getImageUrl(), b.getLinkUrl(), b.getSortOrder());
    }
}
