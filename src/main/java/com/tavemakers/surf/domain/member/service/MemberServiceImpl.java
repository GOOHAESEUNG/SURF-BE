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
import java.util.Locale;


@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

    @Transactional
    public MemberSignupResDTO signup(Long memberKakaoId, MemberSignupReqDTO request) {

        // 1) 정규화
        //final String normalizedEmail = request.getEmail().trim().toLowerCase(Locale.ROOT);
        final String normalizedPhone = request.getPhoneNumber() == null
                ? null
                : request.getPhoneNumber().replaceAll("\\D", "");

        // 2) 선조회
        Member member = memberRepository.findByKakaoId(memberKakaoId).orElse(null);

        // 3) 선행 조건 검증: 사전 등록(카카오 콜백) 미존재 시 에러
        if (member == null) {
            throw new IllegalStateException("사전 등록된 회원이 없습니다. 카카오 로그인 콜백을 먼저 진행하세요.");
        }

        // 4) REGISTERING 상태만 가입 진행 허용
        if (member.getStatus() != MemberStatus.REGISTERING) {
            // 이미 WAITING/APPROVED 등인 경우
            throw new MemberAlreadyExistsException();
        }

        // 5) 가입 정보 반영 (도메인 메서드가 상태 전이까지 수행)
        member.applySignup(request, normalizedPhone);

        // 6) 응답 변환 (영속 엔티티 → DTO)
        return MemberSignupResDTO.from(member);
    }

    @Transactional
    public Boolean needsOnboarding(Long memberKakaoId) {
        Member member = memberRepository.findByKakaoId(memberKakaoId).orElse(null);
        if(member.getStatus().equals(MemberStatus.REGISTERING)) {
            return true;
        }else
            return false;
    }
}
