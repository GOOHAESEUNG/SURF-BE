package com.tavemakers.surf.domain.member.service;

import com.tavemakers.surf.domain.member.entity.Member;
import com.tavemakers.surf.domain.member.entity.enums.MemberStatus;
import com.tavemakers.surf.domain.member.exception.MemberNotFoundException;
import com.tavemakers.surf.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberGetService {

    private final MemberRepository memberRepository;

    public Member getMember(Long memberId) {
        return memberRepository.findByIdAndStatus(memberId, MemberStatus.APPROVED)
                .orElseThrow(MemberNotFoundException::new);
    }

    //회원 조회 - 이름
    public List<Member> getMemberByName(String name) {
        return memberRepository.findByActivityStatusAndName(true,name);

    }

    //활동 여부에 따른 회원 전체 목록 조회
    public List<Member> getMemberByStatus(Boolean status) {
        return memberRepository.findByActivityStatus(status);
    }


}
