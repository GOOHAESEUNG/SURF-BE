package com.tavemakers.surf.domain.member.service;

import com.tavemakers.surf.domain.member.entity.Member;
import com.tavemakers.surf.domain.member.entity.enums.MemberStatus;
import com.tavemakers.surf.domain.member.exception.MemberNotFoundException;
import com.tavemakers.surf.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class MemberGetService {

    private final MemberRepository memberRepository;

    public Member getMemberByApprovedStatus(Long memberId) {
        return memberRepository.findByIdAndStatus(memberId, MemberStatus.APPROVED)
                .orElseThrow(MemberNotFoundException::new);
    }

    public Member getMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);
    }

    //회원 조회 - 이름 기반 - ID 리스트 반환
    public List<Member> getMemberByName(String name) {
        return memberRepository.findByActivityStatusAndName(true, name);
    }

    public Void validateMember(Long memberId) {
        memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);
        return null;
    }


}
