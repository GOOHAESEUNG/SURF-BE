package com.tavemakers.surf.domain.member.service;

import com.tavemakers.surf.domain.member.dto.request.MemberSignupRequest;
import com.tavemakers.surf.domain.member.dto.response.MemberSignupResponse;

public interface MemberService {
    MemberSignupResponse signup(MemberSignupRequest request);
}
