package com.tavemakers.surf.domain.home.controller;

import com.tavemakers.surf.domain.home.dto.request.HomeContentUpsertReqDTO;
import com.tavemakers.surf.domain.home.dto.response.HomeContentResDTO;
import com.tavemakers.surf.domain.home.service.HomeContentService;
import com.tavemakers.surf.global.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import static com.tavemakers.surf.domain.home.controller.ResponseMessage.HOME_CONTENT_READ;
import static com.tavemakers.surf.domain.home.controller.ResponseMessage.HOME_CONTENT_UPSERTED;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/admin/home")
public class HomeAdminController {

    private final HomeContentService homeContentService;

    @GetMapping("/content")
    public ApiResponse<HomeContentResDTO> getContent() {
        HomeContentResDTO response = homeContentService.get();
        return ApiResponse.response(HttpStatus.OK, HOME_CONTENT_READ.getMessage(), response);
    }

    @PutMapping("/content")
    public ApiResponse<HomeContentResDTO> upsertContent(
            @RequestBody @Valid HomeContentUpsertReqDTO req
    ) {
        HomeContentResDTO response = homeContentService.upsert(req);
        return ApiResponse.response(HttpStatus.OK, HOME_CONTENT_UPSERTED.getMessage(), response);
    }
}