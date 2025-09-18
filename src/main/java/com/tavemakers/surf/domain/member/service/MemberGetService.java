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

}
