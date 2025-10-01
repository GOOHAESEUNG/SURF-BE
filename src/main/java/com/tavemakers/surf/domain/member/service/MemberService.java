package com.tavemakers.surf.domain.member.service;

import com.tavemakers.surf.domain.member.dto.request.MemberSignupReqDTO;
import com.tavemakers.surf.domain.member.dto.response.MemberSignupResDTO;
import com.tavemakers.surf.domain.member.entity.Member;

public interface MemberService {
    /**
     * 회원 승인 (ADMIN 전용)
     */
    void approveMember(Member member);

    /**
     * 회원 거절 (ADMIN 전용)
     */
    void rejectMember(Member member);
    MemberSignupResDTO signup(Member member, MemberSignupReqDTO request);
    Boolean needsOnboarding(Member member);
}
