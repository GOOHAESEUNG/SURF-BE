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

    private final MemberGetService memberGetService;

    //추가 정보 입력 회원가입
    @Transactional
    public MemberSignupResDTO signup(Member member, MemberSignupReqDTO request) {
        final String normalizedEmail = request.getEmail().trim().toLowerCase(Locale.ROOT);
        //이메일 중복 체크 해야될거 같은데..프론트에 중복 체크 버튼이 없네..?

        final String normalizedPhone = request.getPhoneNumber() == null
                ? null
                : request.getPhoneNumber().replaceAll("\\D", "");

        member.applySignup(request, normalizedEmail, normalizedPhone);
        return MemberSignupResDTO.from(member);
    }

    //추가 정보 회원가입 온보딩 필요 여부 확인
    @Transactional
    public Boolean needsOnboarding(Member member) {
        if(member.getStatus().equals(MemberStatus.REGISTERING)) {
            return true;
        }else
            return false;
    }
}
