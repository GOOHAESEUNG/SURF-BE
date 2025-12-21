package com.tavemakers.surf.domain.home.dto.request;

import jakarta.validation.constraints.NotBlank;

public record HomeBannerCreateReqDTO(
        @NotBlank
        String imageUrl,   // S3 업로드 완료 후 URL

        String linkUrl
) {

}