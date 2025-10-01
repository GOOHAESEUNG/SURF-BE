package com.tavemakers.surf.domain.member.controller;

import com.tavemakers.surf.domain.member.dto.request.MemberSignupReqDTO;
import com.tavemakers.surf.domain.member.dto.response.MemberSignupResDTO;
import com.tavemakers.surf.domain.member.usecase.MemberUsecase;
import com.tavemakers.surf.global.common.response.ApiResponse;
import com.tavemakers.surf.domain.member.service.MemberService;
import com.tavemakers.surf.global.util.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequiredArgsConstructor
@Tag(name = "자체 회원가입 및 관리자 승인/거절")
public class MemberController {

    private final MemberService memberService;
    private final MemberUsecase memberUsecase;

    @Operation(
            summary = "자체 회원가입 온보딩",
            description = "카카오 로그인 후 추가 정보를 입력하여 회원가입을 완료합니다.")
    @PostMapping("/v1/user/members/signup")
    public ApiResponse<MemberSignupResDTO> signup(@Valid @RequestBody MemberSignupReqDTO request) {
        return ApiResponse.response(
                HttpStatus.CREATED,
                "회원가입 성공",
                memberUsecase.signup(SecurityUtils.getCurrentMemberId(), request)
            );
    }

    @Operation(
            summary = "온보딩(추가 정보 입력) 필요 여부 확인",
            description = "카카오 ID로 회원을 조회하여 추가 정보 입력이 필요한 상태인지 확인합니다.")
    @GetMapping("/v1/user/members/valid-status")
    public ApiResponse<Boolean> checkOnboardingStatus(
            ) {
        return ApiResponse.response(
                HttpStatus.OK,
                ResponseMessage.MEMBER_ONBOARDING_STATUS_CHECK_SUCCESS.getMessage(),
                memberUsecase.needsOnboarding(SecurityUtils.getCurrentMemberId())
        );
    }

    @Operation(
            summary = "회원가입 승인",
            description = "관리자가 회원의 회원가입을 승인합니다.")
    @PatchMapping("/v1/admin/members/{memberId}/approve")
    public ApiResponse<Void> approveMember(@PathVariable Long memberId) {
        memberService.approveMember(memberId);
        return ApiResponse.response(
                HttpStatus.OK,
                "승인되었습니다.",
                null
        );
    }

    @Operation(
            summary = "회원가입 거절",
            description = "관리자가 회원의 회원가입을 거절합니다.")
    @PatchMapping("/v1/admin/members/{memberId}/reject")
    public ApiResponse<Void> rejectMember(@PathVariable Long memberId) {
        memberService.rejectMember(memberId);
        return ApiResponse.response(
                HttpStatus.OK,
                "거절되었습니다.",
                null
        );
    }
}
