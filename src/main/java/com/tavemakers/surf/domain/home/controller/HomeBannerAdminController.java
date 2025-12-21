package com.tavemakers.surf.domain.home.controller;


import com.tavemakers.surf.domain.home.dto.request.HomeBannerCreateReqDTO;
import com.tavemakers.surf.domain.home.dto.request.HomeBannerReorderReqDTO;
import com.tavemakers.surf.domain.home.dto.request.HomeBannerUpdateReqDTO;
import com.tavemakers.surf.domain.home.dto.response.HomeBannerResDTO;
import com.tavemakers.surf.domain.home.service.HomeBannerService;
import com.tavemakers.surf.global.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.tavemakers.surf.domain.home.controller.ResponseMessage.*;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class HomeBannerAdminController {

    private final HomeBannerService homeBannerService;

    @GetMapping("/v1/admin/home/banners")
    public ApiResponse<List<HomeBannerResDTO>> getBanners() {
        List<HomeBannerResDTO> response = homeBannerService.getBanners();
        return ApiResponse.response(HttpStatus.OK, HOME_BANNERS_READ.getMessage(), response);
    }

    @PostMapping("/v1/admin/home/banners")
    public ApiResponse<HomeBannerResDTO> createBanner(
            @RequestBody @Valid HomeBannerCreateReqDTO req
    ) {
        HomeBannerResDTO response = homeBannerService.createBanner(req);
        return ApiResponse.response(HttpStatus.CREATED, HOME_BANNER_CREATED.getMessage(), response);
    }

    @DeleteMapping("/v1/admin/home/banners/{bannerId}")
    public ApiResponse<Void> deleteBanner(
            @PathVariable Long bannerId
    ) {
        homeBannerService.deleteBanner(bannerId);
        return ApiResponse.response(HttpStatus.NO_CONTENT, HOME_BANNER_DELETED.getMessage());
    }

    @PutMapping("/v1/admin/home/banners/order")
    public ApiResponse<List<HomeBannerResDTO>> reorderBanners(
            @RequestBody @Valid HomeBannerReorderReqDTO req
    ) {
        List<HomeBannerResDTO> response = homeBannerService.reorderBanners(req);
        return ApiResponse.response(HttpStatus.OK, HOME_BANNER_REORDERED.getMessage(), response);
    }

    @PatchMapping("/v1/admin/home/banners/{bannerId}")
    public ApiResponse<HomeBannerResDTO> update(
            @PathVariable Long bannerId,
            @RequestBody HomeBannerUpdateReqDTO req
    ) {
        HomeBannerResDTO response = homeBannerService.updateBanner(bannerId, req);
        return ApiResponse.response(HttpStatus.OK, HOME_BANNER_UPDATED.getMessage(), response);
    }
}