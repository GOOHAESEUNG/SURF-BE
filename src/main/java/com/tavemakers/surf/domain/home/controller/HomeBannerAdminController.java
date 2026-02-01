package com.tavemakers.surf.domain.home.controller;


import com.tavemakers.surf.domain.home.dto.request.HomeBannerCreateReqDTO;
import com.tavemakers.surf.domain.home.dto.request.HomeBannerReorderReqDTO;
import com.tavemakers.surf.domain.home.dto.request.HomeBannerUpdateReqDTO;
import com.tavemakers.surf.domain.home.dto.response.HomeBannerResDTO;
import com.tavemakers.surf.domain.home.service.HomeBannerService;
import com.tavemakers.surf.global.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.tavemakers.surf.domain.home.controller.ResponseMessage.*;

@RestController
@RequiredArgsConstructor
@RequestMapping
@Tag(name = "홈 배너 관리", description = "홈 배너 관리 API")
public class HomeBannerAdminController {

    private final HomeBannerService homeBannerService;

    @Operation(summary = "홈 배너 목록 조회", description = "홈 배너 목록을 조회합니다.")
    @GetMapping("/v1/admin/home/banners")
    public ApiResponse<List<HomeBannerResDTO>> getBanners() {
        List<HomeBannerResDTO> response = homeBannerService.getBanners();
        return ApiResponse.response(HttpStatus.OK, HOME_BANNERS_READ.getMessage(), response);
    }

    @Operation(summary = "홈 배너 생성", description = "새로운 홈 배너를 생성합니다.")
    @PostMapping("/v1/admin/home/banners")
    public ApiResponse<HomeBannerResDTO> createBanner(
            @RequestBody @Valid HomeBannerCreateReqDTO req
    ) {
        HomeBannerResDTO response = homeBannerService.createBanner(req);
        return ApiResponse.response(HttpStatus.CREATED, HOME_BANNER_CREATED.getMessage(), response);
    }

    @Operation(summary = "홈 배너 삭제", description = "특정 ID의 홈 배너를 삭제합니다.")
    @DeleteMapping("/v1/admin/home/banners/{bannerId}")
    public ApiResponse<Void> deleteBanner(
            @PathVariable Long bannerId
    ) {
        homeBannerService.deleteBanner(bannerId);
        return ApiResponse.response(HttpStatus.NO_CONTENT, HOME_BANNER_DELETED.getMessage());
    }

    @Operation(summary = "홈 배너 순서 변경", description = "홈 배너의 순서를 변경합니다. 모든 배너의 ID를 포함해야 하며, 배너가 없을 시 빈 배열을 전달해야 합니다.")
    @PutMapping("/v1/admin/home/banners/reorder")
    public ApiResponse<List<HomeBannerResDTO>> reorderBanners(
            @RequestBody @Valid HomeBannerReorderReqDTO req
    ) {
        List<HomeBannerResDTO> response = homeBannerService.reorderBanners(req);
        return ApiResponse.response(HttpStatus.OK, HOME_BANNER_REORDERED.getMessage(), response);
    }

    @Operation(summary = "홈 배너 수정", description = "특정 ID의 홈 배너를 수정합니다.")
    @PutMapping("/v1/admin/home/banners/{bannerId}")
    public ApiResponse<HomeBannerResDTO> update(
            @PathVariable Long bannerId,
            @RequestBody @Valid HomeBannerUpdateReqDTO req
    ) {
        HomeBannerResDTO response = homeBannerService.updateBanner(bannerId, req);
        return ApiResponse.response(HttpStatus.OK, HOME_BANNER_UPDATED.getMessage(), response);
    }

    @PatchMapping("/v1/admin/home/banners/{id}/activate")
    public ApiResponse<HomeBannerResDTO> activate(
            @PathVariable Long id) {
        HomeBannerResDTO response = homeBannerService.activateBanner(id);
        return ApiResponse.response(HttpStatus.OK, HOME_BANNER_STATUS_ACTIVATED.getMessage(), response);
    }

    @PatchMapping("/v1/admin/home/banners/{id}/deactivate")
    public ApiResponse<HomeBannerResDTO> deactivate(
            @PathVariable Long id) {
        HomeBannerResDTO response = homeBannerService.deactivateBanner(id);
        return ApiResponse.response(HttpStatus.OK, HOME_BANNER_STATUS_DEACTIVATED.getMessage(), response);
    }
}