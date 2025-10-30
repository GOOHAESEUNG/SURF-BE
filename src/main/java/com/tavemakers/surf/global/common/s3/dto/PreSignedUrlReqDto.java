package com.tavemakers.surf.global.common.s3.dto;

import java.util.List;

public record PreSignedUrlReqDto(
        List<String> fileNames
) {
}
