package com.tavemakers.surf.domain.member.service;

import com.tavemakers.surf.domain.member.dto.request.MemberSignupReqDTO;
import com.tavemakers.surf.domain.member.dto.response.MemberSignupResDTO;

public interface MemberService {
    MemberSignupResDTO signup(MemberSignupReqDTO request);
}
