package com.tavemakers.surf.domain.member.service;

import com.tavemakers.surf.domain.member.dto.request.MemberSignupReqDTO;
import com.tavemakers.surf.domain.member.dto.response.MemberSignupResDTO;
import com.tavemakers.surf.domain.member.entity.Member;
import com.tavemakers.surf.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.tavemakers.surf.domain.member.entity.enums.MemberStatus;
import java.util.Locale;
import lombok.extern.slf4j.Slf4j;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    //추가 정보 입력 회원가입
    @Transactional
    public MemberSignupResDTO signup(Member member, MemberSignupReqDTO request, String requestId) {
        long start = System.currentTimeMillis();

        // 이메일 및 전화번호 정규화
        final String normalizedEmail = request.getEmail().trim().toLowerCase(Locale.ROOT);
        final String normalizedPhone = request.getPhoneNumber() == null
                ? null
                : request.getPhoneNumber().replaceAll("\\D", "");

        // 회원가입 정보 반영
        member.applySignup(request, normalizedEmail, normalizedPhone);

        MemberSignupResDTO response = MemberSignupResDTO.from(member);
        long duration = System.currentTimeMillis() - start;

        // 성공 로그
        log.info("timestamp={}, event_type=INFO, log_event=signup.succeeded, user_id={}, page_url={}, message={}, request_id={}, actor_role={}, http_method={}, path={}, status={}, duration_ms={}, result={}, props={}",
                java.time.Instant.now().toString(),
                member.getId(),
                "/signup",
                "회원가입 성공",
                requestId,
                "WAITING",
                "POST",
                "/v1/user/members/signup",
                201,
                duration,
                "success",
                String.format("member_id=%d, email=%s", member.getId(), member.getEmail())
        );

        return response;
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

    //추가 정보 회원가입 온보딩 필요 여부 확인
    @Transactional
    public Boolean needsOnboarding(Member member) {
        return member.getStatus().equals(MemberStatus.REGISTERING);
    }

    //회원 승인 여부 체크
    @Transactional
    public MemberStatus memberStatusCheck(Member member) {
        return member.getStatus();
    }
}
