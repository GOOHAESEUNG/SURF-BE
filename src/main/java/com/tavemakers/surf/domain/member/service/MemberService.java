package com.tavemakers.surf.domain.member.service;

import com.tavemakers.surf.domain.member.dto.request.MemberSignupReqDTO;
import com.tavemakers.surf.domain.member.dto.response.MemberSignupResDTO;

public interface MemberService {
    MemberSignupResDTO signup(MemberSignupReqDTO request);

    /**
     * 회원 승인 (ADMIN 전용)
     */
    void approveMember(Long memberId);

    /**
     * 회원 거절 (ADMIN 전용)
     */
    void rejectMember(Long memberId);
}
