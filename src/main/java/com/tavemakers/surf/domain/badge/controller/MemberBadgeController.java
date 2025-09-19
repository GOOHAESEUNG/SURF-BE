package com.tavemakers.surf.domain.badge.controller;

import com.tavemakers.surf.domain.badge.dto.request.MemberBadgeReqDTO;
import com.tavemakers.surf.domain.badge.dto.response.MemberBadgeSliceResDTO;
import com.tavemakers.surf.domain.badge.entity.MemberBadge;
import com.tavemakers.surf.domain.badge.usecase.MemberBadgeUsecase;
import com.tavemakers.surf.global.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import static com.tavemakers.surf.domain.badge.controller.ResponseMessage.MEMBER_BADGE_LIST_CREATED;

@RestController
@RequiredArgsConstructor
public class MemberBadgeController {

    private final MemberBadgeUsecase memberBadgeusecase;

    @PostMapping("/v1/manager/member-badge")
    public ApiResponse<Void> createBadge(@RequestBody @Valid MemberBadgeReqDTO dto) {
        memberBadgeusecase.saveMemberBadgeList(dto);
        return ApiResponse.response(HttpStatus.CREATED, MEMBER_BADGE_LIST_CREATED.getMessage(), null);
    }

}
