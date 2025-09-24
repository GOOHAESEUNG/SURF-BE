package com.tavemakers.surf.domain.member.controller;

import com.tavemakers.surf.domain.member.dto.request.MemberSignupReqDTO;
import com.tavemakers.surf.domain.member.dto.response.MemberSignupResDTO;
import com.tavemakers.surf.domain.member.facade.MemberFacade;
import com.tavemakers.surf.domain.member.service.MemberService;
import com.tavemakers.surf.domain.member.service.MemberServiceImpl;
import com.tavemakers.surf.domain.member.usecase.MemberUsecase;
import com.tavemakers.surf.global.common.response.ApiResponse;
import com.tavemakers.surf.domain.member.service.MemberService;
import com.tavemakers.surf.global.util.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/v1/members")
@RequiredArgsConstructor
@Tag(name = "서비스 내 자체 회원가입 관련")
public class MemberController {

    private final MemberFacade memberFacade;
    private final MemberService memberService;
    private final MemberUsecase memberUsecase;

    @Operation(summary = "자체 회원가입 온보딩")
    @PostMapping("/signup")
    public ApiResponse<MemberSignupResDTO> signup(@Valid @RequestBody MemberSignupReqDTO request) {
        return ApiResponse.response(
                HttpStatus.CREATED,
                "회원가입 성공",
                memberFacade.signup(SecurityUtils.getCurrentMemberId(), request)
            );
    }

    @Operation(summary = "온보딩(추가 정보 입력) 필요 여부 확인", description = "카카오 ID로 회원을 조회하여 추가 정보 입력이 필요한 상태인지 확인합니다.")
    @GetMapping("/valid-status")
    public ApiResponse<Boolean> checkOnboardingStatus(
            ) {
        return ApiResponse.response(
                HttpStatus.OK,
                ResponseMessage.MEMBER_ONBOARDING_STATUS_CHECK_SUCCESS.getMessage(),
                memberUsecase.needsOnboarding(SecurityUtils.getCurrentMemberId())
        );
    }
    @PatchMapping("/{id}/approve")
    public ApiResponse<Void> approveMember(@PathVariable Long id) {
        memberService.approveMember(id);
        return ApiResponse.response(
                HttpStatus.OK,
                "승인되었습니다.",
                null
        );
    }
    @PatchMapping("/{id}/reject")
    public ApiResponse<Void> rejectMember(@PathVariable Long id) {
        memberService.rejectMember(id);
        return ApiResponse.response(
                HttpStatus.OK,
                "거절되었습니다.",
                null
        );
    }
}
