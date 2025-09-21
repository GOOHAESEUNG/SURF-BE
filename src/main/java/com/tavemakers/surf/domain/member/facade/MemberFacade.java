package com.tavemakers.surf.domain.member.facade;

import com.tavemakers.surf.domain.member.dto.request.MemberSignupRequest;
import com.tavemakers.surf.domain.member.dto.response.MemberSignupResponse;
import com.tavemakers.surf.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberFacade {

    private final MemberService memberService;

    public MemberSignupResponse signup(MemberSignupRequest request) {
        return memberService.signup(request);
    }
}
