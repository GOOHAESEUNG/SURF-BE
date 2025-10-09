package com.tavemakers.surf.domain.member.controller;

import com.tavemakers.surf.domain.member.dto.request.MemberSignupReqDTO;
import com.tavemakers.surf.domain.member.dto.response.MemberSignupResDTO;
import com.tavemakers.surf.domain.member.exception.MemberAlreadyExistsException;
import com.tavemakers.surf.domain.member.dto.response.OnboardingCheckResDTO;
import com.tavemakers.surf.domain.member.usecase.MemberAdminUsecase;
import com.tavemakers.surf.domain.member.usecase.MemberUsecase;
import com.tavemakers.surf.global.common.response.ApiResponse;
import com.tavemakers.surf.domain.member.service.MemberService;
import com.tavemakers.surf.global.util.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "자체 회원가입 및 관리자 승인/거절")
public class MemberController {

    private final MemberUsecase memberUsecase;
    private final MemberAdminUsecase memberAdminUsecase;

    @Operation(
            summary = "자체 회원가입 온보딩",
            description = "카카오 로그인 후 추가 정보를 입력하여 회원가입을 완료합니다.")
    @PostMapping("/v1/user/members/signup")
    public ApiResponse<MemberSignupResDTO> signup(@Valid @RequestBody MemberSignupReqDTO request) {
        long start = System.currentTimeMillis();
        String requestId = UUID.randomUUID().toString();
        Long userId = SecurityUtils.getCurrentMemberId();

        try {
            ApiResponse<MemberSignupResDTO> response = ApiResponse.response(
                    HttpStatus.CREATED,
                    "회원가입 성공",
                    memberUsecase.signup(userId, request)
            );

            long duration = System.currentTimeMillis() - start;
            log.info("timestamp={}, event_type=INFO, log_event=signup.create, user_id={}, page_url={}, message={}, request_id={}, actor_role={}, http_method={}, path={}, status={}, duration_ms={}, result={}, props={}",
                    java.time.Instant.now().toString(),
                    userId,
                    "/signup",
                    "회원가입 요청 처리 완료",
                    requestId,
                    "WAITING",
                    "POST",
                    "/v1/user/members/signup",
                    201,
                    duration,
                    "success",
                    String.format("name_len=%d, tracks_count=%d, university=%s",
                            request.getName().length(),
                            request.getTracks().size(),
                            request.getUniversity())
            );

            return response;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - start;

            // 예외 유형별 상태 코드와 사유 자동 구분
            int statusCode;
            String errorReason;

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

            log.error("timestamp={}, event_type=ERROR, log_event=signup.failed, user_id={}, page_url={}, message={}, request_id={}, actor_role={}, http_method={}, path={}, status={}, duration_ms={}, result={}, props={}",
                    java.time.Instant.now().toString(),
                    userId,
                    "/signup",
                    "회원가입 실패: " + e.getMessage(),
                    requestId,
                    "REGISTERING",
                    "POST",
                    "/v1/user/members/signup",
                    statusCode,
                    duration,
                    "fail",
                    String.format("error_code=%d, error_reason=%s, error_msg=%s",
                            statusCode,
                            errorReason,
                            e.getMessage())
            );
            throw e;
        }
    }

    @Operation(
            summary = "온보딩(추가 정보 입력) 필요 여부 확인",
            description = "카카오 ID로 회원을 조회하여 추가 정보 입력이 필요한 상태인지 확인합니다.")
    @GetMapping("/v1/user/members/valid-status")
    public ApiResponse<Boolean> checkOnboardingStatus() {
        long start = System.currentTimeMillis();
        String requestId = UUID.randomUUID().toString();
        Long userId = SecurityUtils.getCurrentMemberId();

        OnboardingCheckResDTO responseDto = memberUsecase.needsOnboarding(userId);
        Boolean needOnboarding = responseDto.getNeedOnboarding(); // 로그용 값 추출
        long duration = System.currentTimeMillis() - start;

        log.info("timestamp={}, event_type=INFO, log_event=onboarding.valid_status, user_id={}, page_url={}, message={}, request_id={}, actor_role={}, http_method={}, path={}, status={}, duration_ms={}, result={}, props={}",
                java.time.Instant.now().toString(),
                userId,
                "/signup/valid-status",
                "온보딩 필요 여부 조회",
                requestId,
                "REGISTERING",
                "GET",
                "/v1/user/members/valid-status",
                200,
                duration,
                "success",
                String.format("need_onboarding=%s", needOnboarding)
        );

        return ApiResponse.response(
                HttpStatus.OK,
                ResponseMessage.MEMBER_ONBOARDING_STATUS_CHECK_SUCCESS.getMessage(),
                needOnboarding
        );
    }

    @Operation(
            summary = "회원가입 승인",
            description = "관리자가 회원의 회원가입을 승인합니다.")
    @PatchMapping("/v1/admin/members/{memberId}/approve")
    public ApiResponse<Void> approveMember(@PathVariable Long memberId) {
        long start = System.currentTimeMillis();
        String requestId = UUID.randomUUID().toString();
        Long approverId = SecurityUtils.getCurrentMemberId();

        memberAdminUsecase.approveMember(memberId);

        long duration = System.currentTimeMillis() - start;
        log.info("timestamp={}, event_type=INFO, log_event=signup.approve, user_id={}, page_url={}, message={}, request_id={}, actor_role={}, http_method={}, path={}, status={}, duration_ms={}, result={}, props={}",
                java.time.Instant.now().toString(),
                approverId,
                "/admin/members/approve",
                "회원가입 승인 처리",
                requestId,
                "APPROVED",
                "PATCH",
                "/v1/admin/members/{memberId}/approve",
                200,
                duration,
                "success",
                String.format("member_id=%d, approver_id=%d", memberId, approverId)
        );

        return ApiResponse.response(HttpStatus.OK, "승인되었습니다.", null);
    }

    @Operation(
            summary = "회원가입 거절",
            description = "관리자가 회원의 회원가입을 거절합니다.")
    @PatchMapping("/v1/admin/members/{memberId}/reject")
    public ApiResponse<Void> rejectMember(@PathVariable Long memberId) {
        long start = System.currentTimeMillis();
        String requestId = UUID.randomUUID().toString();
        Long approverId = SecurityUtils.getCurrentMemberId();

        memberAdminUsecase.rejectMember(memberId);

        long duration = System.currentTimeMillis() - start;
        log.info("timestamp={}, event_type=INFO, log_event=signup.reject, user_id={}, page_url={}, message={}, request_id={}, actor_role={}, http_method={}, path={}, status={}, duration_ms={}, result={}, props={}",
                java.time.Instant.now().toString(),
                approverId,
                "/admin/members/reject",
                "회원가입 거절 처리",
                requestId,
                "REJECTED",
                "PATCH",
                "/v1/admin/members/{memberId}/reject",
                200,
                duration,
                "success",
                String.format("member_id=%d, approver_id=%d", memberId, approverId)
        );

        return ApiResponse.response(HttpStatus.OK, "거절되었습니다.", null);
    }
}
