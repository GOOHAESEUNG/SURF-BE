package com.tavemakers.surf.domain.member.controller;

import com.tavemakers.surf.domain.member.dto.request.MemberSignupReqDTO;
import com.tavemakers.surf.domain.member.dto.response.MemberSignupResDTO;
import com.tavemakers.surf.domain.member.facade.MemberFacade;
import com.tavemakers.surf.domain.member.service.MemberService;
import com.tavemakers.surf.domain.member.service.MemberServiceImpl;
import com.tavemakers.surf.global.common.response.ApiResponse;
import com.tavemakers.surf.global.util.SecurityUtils;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberFacade memberFacade;
    private final MemberServiceImpl memberServicImpl;

    @PostMapping("/signup")
    public ApiResponse<MemberSignupResDTO> signup(@Valid @RequestBody MemberSignupReqDTO request,
                                                  @Parameter Long memberKakaoId) {
        return ApiResponse.response(
                HttpStatus.CREATED,
                "회원가입 성공",
                memberFacade.signup(memberKakaoId, request)
            );
    }

    @GetMapping("/valid-status")
    public ApiResponse<Boolean> validStatus(@Valid Long memberKakaoId) {
            memberServicImpl.validateMemberRegistering(memberKakaoId);
    }
}
