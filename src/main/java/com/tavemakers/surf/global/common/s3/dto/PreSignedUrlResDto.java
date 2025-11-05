package com.tavemakers.surf.global.common.s3.dto;

import lombok.Builder;

@Builder
public record PreSignedUrlResDto(
        String key,
        String preSignedUrl
) {
    public static PreSignedUrlResDto from(String key, String preSignedUrl) {
        return PreSignedUrlResDto.builder()
                .key(key)
                .preSignedUrl(preSignedUrl)
                .build();
    }
}
