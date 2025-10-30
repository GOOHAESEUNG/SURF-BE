package com.tavemakers.surf.domain.member.service;

import com.tavemakers.surf.domain.member.dto.request.MemberSignupReqDTO;
import com.tavemakers.surf.domain.member.dto.response.MemberSignupResDTO;
import com.tavemakers.surf.domain.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.tavemakers.surf.domain.member.entity.enums.MemberStatus;
import java.util.Locale;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    @Override
    @Transactional
    public MemberSignupResDTO signup(
            Member member,
            MemberSignupReqDTO request
    ) {
        // 이메일 및 전화번호 정규화
        final String normalizedEmail = request.getEmail().trim().toLowerCase(Locale.ROOT);
        final String normalizedPhone = request.getPhoneNumber() == null
                ? null
                : request.getPhoneNumber().replaceAll("\\D", "");

        // 회원가입 정보 반영
        member.applySignup(request, normalizedEmail, normalizedPhone);

        return MemberSignupResDTO.from(member);
    }

    /** 회원 승인 */
    @Override
    @Transactional
    public void approveMember(Member member) {
        member.approve();
    }

    /** 회원 거절 */
    @Override
    @Transactional
    public void rejectMember(Member member) {
        member.reject();
    }

    /** 온보딩 필요 여부 확인 */
    @Override
    @Transactional(readOnly = true)
    public Boolean needsOnboarding(Member member) {
        return member.getStatus().equals(MemberStatus.REGISTERING);
    }

    /** 회원 상태 조회 */
    @Override
    @Transactional(readOnly = true)
    public MemberStatus memberStatusCheck(Member member) {
        return member.getStatus();
    }
}
