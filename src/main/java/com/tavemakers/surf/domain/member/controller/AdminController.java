package com.tavemakers.surf.domain.member.controller;

import com.tavemakers.surf.domain.member.dto.request.RoleChangeRequestDto;
import com.tavemakers.surf.domain.member.usecase.MemberAdminUsecase;
import io.swagger.v3.oas.annotations.Operation;
import com.tavemakers.surf.global.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "관리자", description = "관리자용 API")
@RestController
@RequestMapping("/v1/admin/members")
@RequiredArgsConstructor
public class AdminController {

    private final MemberAdminUsecase memberAdminUsecase;

    @Operation(summary = "회원 역할 변경", description = "특정 회원의 역할을 변경합니다.")
    @PatchMapping("/{memberId}/role")
    public ApiResponse<Void> changeMemberRole(
            @PathVariable Long memberId,
            @RequestBody @Valid RoleChangeRequestDto request) {

        memberAdminUsecase.changeRole(memberId, request.role());
        return ApiResponse.response(HttpStatus.OK, "회원 역할이 성공적으로 변경되었습니다.",null);
    }
}
