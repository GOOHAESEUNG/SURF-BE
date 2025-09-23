package com.tavemakers.surf.domain.member.service;

import com.tavemakers.surf.domain.member.dto.request.MemberSignupReqDTO;
import com.tavemakers.surf.domain.member.dto.response.MemberSignupResDTO;
import com.tavemakers.surf.domain.member.entity.Member;
import com.tavemakers.surf.domain.member.exception.MemberNotFoundException;
import com.tavemakers.surf.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
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

        // 1) 이메일 입력 정규화
        final String normalizedEmail = request.getEmail().trim().toLowerCase(Locale.ROOT);
        final String normalizedPhone = request.getPhoneNumber() == null
                        ? null
                        : request.getPhoneNumber().replaceAll("\\D", ""); // 하이픈 등 제거

        // 2) 중복 체크 (정규화된 이메일 기준)
        if (memberRepository.existsByEmail(normalizedEmail)) {
            throw new MemberAlreadyExistsException();
        }

        // 3) Member 엔티티 생성
        Member member = Member.create(request, normalizedEmail, normalizedPhone);

        // 4) DB 저장 (+ 최종 방어막: 유니크 위반 캐치)
       Member saved;
        try {
            saved = memberRepository.save(member);
        } catch (DataIntegrityViolationException e) {
            // 이메일 유니크 제약 위반 등
                    throw new MemberAlreadyExistsException();
        }

        // 5) 응답 DTO 변환
        return MemberSignupResDTO.from(saved);
    }

    /** 회원 승인 */
    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void approveMember(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));

        member.approve();
    }

    /** 회원 거절 */
    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void rejectMember(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));

        member.reject();
    }
}
