package com.tavemakers.surf.domain.badge.controller;

import com.tavemakers.surf.domain.badge.dto.request.MemberBadgeReqDTO;
import com.tavemakers.surf.domain.badge.dto.response.MemberBadgeSliceResDTO;
import com.tavemakers.surf.domain.badge.entity.MemberBadge;
import com.tavemakers.surf.domain.badge.usecase.MemberBadgeUsecase;
import com.tavemakers.surf.global.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import static com.tavemakers.surf.domain.badge.controller.ResponseMessage.MEMBER_BADGE_LIST_CREATED;
import static com.tavemakers.surf.domain.badge.controller.ResponseMessage.MEMBER_BADGE_LIST_READ;

@RestController
@RequiredArgsConstructor
@Tag(name = "활동뱃지")
public class MemberBadgeController {

    private final MemberBadgeUsecase memberBadgeusecase;

    @Operation(summary = "활동 뱃지 부여")
    @PostMapping("/v1/manager/member-badge")
    public ApiResponse<Void> createBadge(@RequestBody @Valid MemberBadgeReqDTO dto) {
        memberBadgeusecase.saveMemberBadgeList(dto);
        return ApiResponse.response(HttpStatus.CREATED, MEMBER_BADGE_LIST_CREATED.getMessage(), null);
    }

    @Operation(summary = "마이페이지 활동 뱃지 조회")
    @GetMapping("/v1/member/{memberId}/member-badge")
    public ApiResponse<MemberBadgeSliceResDTO> getBadge(
            @PathVariable("memberId") Long memberId,
            @RequestParam int pageSize,
            @RequestParam int pageNum
    ) {
        MemberBadgeSliceResDTO response = memberBadgeusecase.getMemberBadgeWithSlice(memberId, pageSize, pageNum);
        return ApiResponse.response(HttpStatus.OK, MEMBER_BADGE_LIST_READ.getMessage(), response);
    }

}
