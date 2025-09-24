package com.tavemakers.surf.domain.member.facade;

import com.tavemakers.surf.domain.member.dto.request.MemberSignupReqDTO;
import com.tavemakers.surf.domain.member.dto.response.MemberSignupResDTO;
import com.tavemakers.surf.domain.member.entity.Member;
import com.tavemakers.surf.domain.member.service.MemberGetService;
import com.tavemakers.surf.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberFacade {

    private final MemberService memberService;
    private final MemberGetService memberGetService;

    public MemberSignupResDTO signup(Long memberId,MemberSignupReqDTO request) {
        Member member = memberGetService.getMember(memberId);
        return memberService.signup(member, request);
    }
}
