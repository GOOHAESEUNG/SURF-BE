package com.tavemakers.surf.domain.member.controller;

import com.tavemakers.surf.domain.member.dto.request.MemberSignupReqDTO;
import com.tavemakers.surf.domain.member.dto.response.MemberSignupResDTO;
import com.tavemakers.surf.domain.member.facade.MemberFacade;
import com.tavemakers.surf.global.common.response.ApiResponse;
import com.tavemakers.surf.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberFacade memberFacade;
    private final MemberService memberService;

    @PostMapping("/signup")
    public ApiResponse<MemberSignupResDTO> signup(@Valid @RequestBody MemberSignupReqDTO request) {
        return ApiResponse.response(
                HttpStatus.CREATED,
                "회원가입 성공",
                memberFacade.signup(request)
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
