package com.tavemakers.surf.global.common.s3.dto;

import lombok.Builder;

@Builder
public record PreSignedUrlResponse(
        String key,
        String preSignedUrl
) {
    public static PreSignedUrlResponse from(String key, String preSignedUrl) {
        return PreSignedUrlResponse.builder()
                .key(key)
                .preSignedUrl(preSignedUrl)
                .build();
    }
}
