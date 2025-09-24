package com.tavemakers.surf.domain.member.service;

import com.tavemakers.surf.domain.member.dto.request.MemberSignupReqDTO;
import com.tavemakers.surf.domain.member.dto.response.MemberSignupResDTO;
import com.tavemakers.surf.domain.member.entity.Member;
import com.tavemakers.surf.domain.member.exception.MemberNotFoundException;
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
    private final MemberGetService memberGetService;

    //추가 정보 입력 회원가입
    @Transactional
    public MemberSignupResDTO signup(Long memberId, MemberSignupReqDTO request) {

        Member member = memberGetService.getMember(memberId);

        final String normalizedEmail = request.getEmail().trim().toLowerCase(Locale.ROOT);

        final String normalizedPhone = request.getPhoneNumber() == null
                ? null
                : request.getPhoneNumber().replaceAll("\\D", "");

        member.applySignup(request, normalizedEmail, normalizedPhone);
        return MemberSignupResDTO.from(member);
    }

    //추가 정보 회원가입 온보딩 필요 여부 확인
    @Transactional
    public Boolean needsOnboarding(Long memberId) {
        Member member = memberGetService.getMember(memberId);
        if(member.getStatus().equals(MemberStatus.REGISTERING)) {
            return true;
        }else
            return false;
    }
}
