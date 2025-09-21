package com.tavemakers.surf.domain.member.controller;

import com.tavemakers.surf.domain.member.dto.request.MemberSignupRequest;
import com.tavemakers.surf.domain.member.dto.response.MemberSignupResponse;
import com.tavemakers.surf.domain.member.facade.MemberFacade;
import com.tavemakers.surf.global.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberFacade memberFacade;

    @PostMapping("/signup")
    public ApiResponse<MemberSignupResponse> signup(@Valid @RequestBody MemberSignupRequest request) {
        return ApiResponse.response(
                HttpStatus.CREATED,
                "회원가입 성공",
                memberFacade.signup(request)
        );
    }
}
