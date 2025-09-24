package com.tavemakers.surf.domain.member.service;

import com.tavemakers.surf.domain.member.dto.request.MemberSignupReqDTO;
import com.tavemakers.surf.domain.member.dto.response.MemberSignupResDTO;
import com.tavemakers.surf.domain.member.entity.Member;

public interface MemberService {
    MemberSignupResDTO signup(Member member, MemberSignupReqDTO request);
    Boolean needsOnboarding(Member member);
}
