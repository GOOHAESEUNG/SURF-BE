package com.tavemakers.surf.domain.member.controller;

import com.tavemakers.surf.domain.member.dto.request.MemberSignupReqDTO;
import com.tavemakers.surf.domain.member.dto.response.MemberSignupResDTO;
import com.tavemakers.surf.domain.member.exception.MemberAlreadyExistsException;
import com.tavemakers.surf.domain.member.dto.response.OnboardingCheckResDTO;
import com.tavemakers.surf.domain.member.usecase.MemberAdminUsecase;
import com.tavemakers.surf.domain.member.usecase.MemberUsecase;
import com.tavemakers.surf.global.common.response.ApiResponse;
import com.tavemakers.surf.global.logging.LogParam;
import com.tavemakers.surf.global.logging.LogEventEmitter;
import com.tavemakers.surf.global.util.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "자체 회원가입 및 관리자 승인/거절")
public class MemberController {

    private final MemberUsecase memberUsecase;
    private final MemberAdminUsecase memberAdminUsecase;
    private final LogEventEmitter logEventEmitter;

    /**
     * 1) 자체 회원가입 온보딩
     * event: signup.create / signup.succeeded & signup.failed
     */
    @Operation(
            summary = "자체 회원가입 온보딩",
            description = "카카오 로그인 후 추가 정보를 입력하여 회원가입을 요청합니다."
    )
    @PostMapping("/v1/user/members/signup")
    public ApiResponse<MemberSignupResDTO> signup(
            @Valid @RequestBody MemberSignupReqDTO request,
            @LogParam("request_id") String requestId
    ) {
        Long userId = SecurityUtils.getCurrentMemberId();

        try {
            ApiResponse<MemberSignupResDTO> response = ApiResponse.response(
                    HttpStatus.CREATED,
                    "회원가입 요청 접수",
                    memberUsecase.signup(userId, request, requestId)
            );

            return response;
        } catch (Exception e) {

            int statusCode;
            String errorReason = e.getMessage();

            if (e instanceof MemberAlreadyExistsException) {
                statusCode = 409;
                errorReason = "MEMBER_ALREADY_EXISTS";
            } else if (e instanceof IllegalArgumentException) {
                statusCode = 400;
                errorReason = "INVALID_ARGUMENT";
            } else {
                statusCode = 500;
                errorReason = "INTERNAL_SERVER_ERROR";
            }

            memberUsecase.signupFailed(userId, statusCode, errorReason);

            return null;
        }
    }

    /**
     * 2) 온보딩(추가 정보 입력) 필요 여부 확인
     * event: onboarding.valid_status
     */
    @Operation(
            summary = "온보딩(추가 정보 입력) 필요 여부 확인",
            description = "카카오 ID로 회원을 조회하여 추가 정보 입력이 필요한 상태인지 확인합니다."
    )
    @GetMapping("/v1/user/members/valid-status")
    public ApiResponse<OnboardingCheckResDTO> checkOnboardingStatus() {
        Long userId = SecurityUtils.getCurrentMemberId();
        OnboardingCheckResDTO dto = memberUsecase.needsOnboarding(userId);

        logEventEmitter.emit("onboarding.valid_status", dto.buildProps());

        return ApiResponse.response(
                HttpStatus.OK,
                ResponseMessage.MEMBER_ONBOARDING_STATUS_CHECK_SUCCESS.getMessage(),
                dto
        );
    }

    /**
     * 3) (관리자의) 회원가입 승인
     * event: signup.approve
     */
    @Operation(
            summary = "회원가입 승인",
            description = "관리자가 회원의 회원가입을 승인합니다."
    )
    @PatchMapping("/v1/admin/members/{memberId}/approve")
    public ApiResponse<Void> approveMember(
            @PathVariable Long memberId
    ) {
        Long approverId = SecurityUtils.getCurrentMemberId();
        memberAdminUsecase.approveMember(memberId, approverId);

        return ApiResponse.response(HttpStatus.OK, "승인되었습니다.", null);
    }

    /**
     * 4) (관리자의) 회원가입 거절
     * event: signup.reject
     */
    @Operation(
            summary = "회원가입 거절",
            description = "관리자가 회원의 회원가입을 거절합니다."
    )
    @PatchMapping("/v1/admin/members/{memberId}/reject")
    public ApiResponse<Void> rejectMember(
            @PathVariable Long memberId
    ) {
        Long approverId = SecurityUtils.getCurrentMemberId();
        memberAdminUsecase.rejectMember(memberId, approverId);

        return ApiResponse.response(HttpStatus.OK, "거절되었습니다.", null);
    }
}
