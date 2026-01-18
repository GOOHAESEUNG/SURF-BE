package com.tavemakers.surf.domain.member.controller;

import com.tavemakers.surf.domain.member.dto.request.AdminPageLoginReqDto;
import com.tavemakers.surf.domain.member.dto.request.PasswordReqDto;
import com.tavemakers.surf.domain.member.dto.request.RoleChangeRequestDto;
import com.tavemakers.surf.domain.member.dto.response.AdminPageLoginResDto;
import com.tavemakers.surf.domain.member.dto.response.MemberRegistrationSliceResDTO;
import com.tavemakers.surf.domain.member.usecase.MemberAdminUsecase;
import io.swagger.v3.oas.annotations.Operation;
import com.tavemakers.surf.global.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import static com.tavemakers.surf.domain.member.controller.ResponseMessage.*;

@Tag(name = "관리자", description = "관리자용 API")
@RestController
@RequestMapping
@RequiredArgsConstructor
public class AdminController {

    private final MemberAdminUsecase memberAdminUsecase;

    @Operation(summary = "회원 역할 변경", description = "특정 회원의 역할을 변경합니다.")
    @PatchMapping("/v1/admin/members/{memberId}/role")
    public ApiResponse<Void> changeMemberRole(
            @PathVariable Long memberId,
            @RequestBody @Valid RoleChangeRequestDto request) {

        memberAdminUsecase.changeRole(memberId, request.role());
        return ApiResponse.response(HttpStatus.OK, "회원 역할이 성공적으로 변경되었습니다.",null);
    }

    @Operation(summary = "비밀번호 설정", description = "관리자의 비밀번호를 설정합니다.")
    @PatchMapping("/v1/manager/password")
    public ApiResponse<Void> setUpPassword(@RequestBody PasswordReqDto dto) {
        memberAdminUsecase.setUpPassword(dto);
        return ApiResponse.response(HttpStatus.OK, MANAGER_PASSWORD_SET_UP_SUCCESS.getMessage(),null);
    }

    @Operation(summary = "관리자 페이지 로그인", description = "관리자 페이지에 로그인합니다.")
    @PostMapping("/v1/manager/sign-in")
    public ApiResponse<AdminPageLoginResDto> loginAdminPage(
            @RequestBody AdminPageLoginReqDto dto,
            HttpServletResponse response
    ) {
        AdminPageLoginResDto data = memberAdminUsecase.loginAdminHomePage(dto, response);
        return ApiResponse.response(HttpStatus.OK, ADMIN_PAGE_LOGIN_SUCCESS.getMessage(),data);
    }

    @Operation(summary = "가입신청 목록", description = "가입신청 목록을 조회합니다.")
    @GetMapping("/v1/manager/registration-list")
    public ApiResponse<MemberRegistrationSliceResDTO> readRegistrationList (
            @RequestParam int pageSize,
            @RequestParam int pageNum
    ) {
        MemberRegistrationSliceResDTO data = memberAdminUsecase.readRegistrationList(pageSize, pageNum);
        return ApiResponse.response(HttpStatus.OK, REGISTRATION_LIST_READ.getMessage(), data);
    }

}
