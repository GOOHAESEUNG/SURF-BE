package com.tavemakers.surf.domain.member.service;

import com.tavemakers.surf.domain.member.entity.Member;
import com.tavemakers.surf.domain.member.entity.enums.MemberStatus;
import com.tavemakers.surf.domain.member.exception.MemberNotFoundException;
import com.tavemakers.surf.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberGetService {

    private final MemberRepository memberRepository;

    public Member getMember(Long memberId) {
        return memberRepository.findByIdAndStatus(memberId, MemberStatus.APPROVED)
                .orElseThrow(MemberNotFoundException::new);
    }

    //회원 조회 - 이름
    public Member getMemberByName(String name) {
        return memberRepository.findByNameAndActivityStatus(true,name)
                .orElse(null); //검색한 이름의 유저가 없을 경우
    }

}
