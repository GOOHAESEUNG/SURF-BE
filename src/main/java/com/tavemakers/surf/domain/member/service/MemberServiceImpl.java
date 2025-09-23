package com.tavemakers.surf.domain.member.service;

import com.tavemakers.surf.domain.member.dto.request.MemberSignupReqDTO;
import com.tavemakers.surf.domain.member.dto.response.MemberSignupResDTO;
import com.tavemakers.surf.domain.member.entity.Member;
import com.tavemakers.surf.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.tavemakers.surf.domain.member.exception.MemberAlreadyExistsException;
import com.tavemakers.surf.domain.member.entity.enums.MemberStatus;
import com.tavemakers.surf.domain.member.entity.enums.MemberRole;
import java.util.Locale;
import org.springframework.dao.DataIntegrityViolationException;


@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

    @Transactional
    public MemberSignupResDTO signup(MemberSignupReqDTO request) {

        // 1) 정규화
        final String normalizedEmail = request.getEmail().trim().toLowerCase(Locale.ROOT);
        final String normalizedPhone = request.getPhoneNumber() == null
                ? null
                : request.getPhoneNumber().replaceAll("\\D", "");

        // 2) 선조회
        Member member = memberRepository.findByEmail(normalizedEmail).orElse(null);

        if (member != null) {
            // 2-1) 이미 존재: REGISTERING이면 가입 정보 반영
            if (member.getStatus() == MemberStatus.REGISTERING) {
                member.applySignup(request, normalizedPhone); // ← 변경 감지로 업데이트
                return MemberSignupResDTO.from(member);
            }
            // 2-2) 이미 가입 진행된 계정이면 예외
            throw new MemberAlreadyExistsException();
        }
    }
}
