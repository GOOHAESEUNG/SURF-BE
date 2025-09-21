package com.tavemakers.surf.domain.member.service;

import com.tavemakers.surf.domain.member.dto.request.MemberSignupReqDTO;
import com.tavemakers.surf.domain.member.dto.response.MemberSignupResDTO;
import com.tavemakers.surf.domain.member.entity.Member;
import com.tavemakers.surf.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.tavemakers.surf.domain.member.exception.MemberAlreadyExistsException;
import com.tavemakers.surf.domain.member.entity.enums.MemberStatus;
import com.tavemakers.surf.domain.member.entity.enums.MemberRole;


@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

    @Override
    public MemberSignupResDTO signup(MemberSignupReqDTO request) {

        // 1. 중복 가입을 막기 위한 이메일 중복 체크
        if (memberRepository.existsByEmail(request.getEmail())) {
            throw new MemberAlreadyExistsException();
        }

        // 2. Member 엔티티 생성
        Member member = Member.builder()
                .name(request.getName())
                .university(request.getUniversity())
                .graduateSchool(request.getGraduateSchool())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .profileImageUrl(request.getProfileImageUrl())
                .status(MemberStatus.WAITING)   // 자동
                .role(MemberRole.MEMBER)        // 자동
                .activityStatus(true)           // 자동
                .build();

        // 3. DB 저장
        Member saved = memberRepository.save(member);

        // 4. 응답 DTO 변환
        return MemberSignupResDTO.of(saved);
    }
}
