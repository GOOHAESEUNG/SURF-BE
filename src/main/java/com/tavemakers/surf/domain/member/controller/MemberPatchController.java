package com.tavemakers.surf.domain.member.controller;

import com.tavemakers.surf.domain.member.dto.MemberSearchResDTO;
import com.tavemakers.surf.domain.member.dto.request.ProfileUpdateRequestDTO;
import com.tavemakers.surf.domain.member.usecase.MemberUsecase;
import com.tavemakers.surf.global.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/member")
@Tag(name = "회원 정보 수정", description = "회원 정보 수정 관련 API")
public class MemberPatchController {

    private final MemberUsecase memberUsecase;

    @Operation(
            summary = "회원 프로필 수정하기",
            description = "마이페이지에서 프로필을 수정하는 API 입니다.")
    @PatchMapping("/profile-update")
    public ApiResponse<List<ProfileUpdateRequestDTO>> updateProfile(
            @Valid @RequestBody ProfileUpdateRequestDTO profileUpdateRequestDTO
    ) {
        memberUsecase.updateProfile(1L, profileUpdateRequestDTO); //멤버 아이디 임시
        return ApiResponse.response(
                HttpStatus.OK,
                ResponseMessage.MYPAGE_PROFILE_UPDATE_SUCCESS.getMessage(),
                null);
    }
}
