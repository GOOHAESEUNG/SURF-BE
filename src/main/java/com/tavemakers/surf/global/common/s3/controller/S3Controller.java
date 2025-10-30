package com.tavemakers.surf.global.common.s3.controller;

import com.tavemakers.surf.global.common.response.ApiResponse;
import com.tavemakers.surf.global.common.s3.service.S3Service;
import com.tavemakers.surf.global.common.s3.dto.PreSignedUrlRequestDto;
import com.tavemakers.surf.global.common.s3.dto.PreSignedUrlResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.tavemakers.surf.global.common.s3.controller.ResponseMessage.PRE_SIGNED_URL_GENERATED;

@RestController
@RequiredArgsConstructor
public class S3Controller {

    private final S3Service s3Service;

    @PostMapping("/v1/user/presigned-url")
    public ApiResponse<List<PreSignedUrlResponse>> generatePutPreSignedUrlList(
            @RequestBody PreSignedUrlRequestDto dto
    ) {
        List<PreSignedUrlResponse> response = s3Service.generatePreSignedUrlList(dto.fileNames());
        return ApiResponse.response(HttpStatus.OK, PRE_SIGNED_URL_GENERATED.getMessage(), response);
    }

}
