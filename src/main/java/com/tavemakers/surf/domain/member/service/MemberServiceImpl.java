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

        // 3) REGISTERING 상태만 가입 진행 허용
        if (member.getStatus() != MemberStatus.REGISTERING) {
            // 이미 WAITING/APPROVED 등인 경우
            throw new MemberAlreadyExistsException();
        }

        // 4) 가입 정보 반영 (도메인 메서드가 상태 전이까지 수행)
        member.applySignup(request, normalizedPhone);

        // 5) 응답 변환 (영속 엔티티 → DTO)
        return MemberSignupResDTO.from(member);
    }
}
