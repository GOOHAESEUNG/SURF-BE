package com.tavemakers.surf.domain.member.service;

import com.tavemakers.surf.domain.member.dto.request.MemberSignupReqDTO;
import com.tavemakers.surf.domain.member.dto.response.MemberSignupResDTO;
import com.tavemakers.surf.domain.member.entity.Member;
import com.tavemakers.surf.domain.member.entity.enums.MemberStatus;

public interface MemberService {

    /** 회원 승인 (관리자) */
    void approveMember(Member member);

    /** 회원 거절 (관리자) */
    void rejectMember(Member member);

    /** 자체 회원가입 신청 완료 */
    MemberSignupResDTO signup(Member member, MemberSignupReqDTO request);

    /** 온보딩 필요 여부 확인 */
    Boolean needsOnboarding(Member member);

    /** 회원 상태 조회 */
    MemberStatus memberStatusCheck(Member member);

}
