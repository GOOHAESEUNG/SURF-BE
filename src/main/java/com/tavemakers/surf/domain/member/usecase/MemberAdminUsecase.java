package com.tavemakers.surf.domain.member.usecase;

import com.tavemakers.surf.domain.member.entity.Member;
import com.tavemakers.surf.domain.member.entity.enums.MemberRole;
import com.tavemakers.surf.domain.member.service.MemberGetService;
import com.tavemakers.surf.domain.member.service.MemberPatchService;
import com.tavemakers.surf.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberAdminUsecase {

    private final MemberPatchService memberPatchService;
    private final MemberGetService memberGetService;
    private final MemberService memberService;

    @Transactional
    public void changeRole (Long memberId, MemberRole role) {
        Member member = memberGetService.getMember(memberId);
        memberPatchService.grantRole(member, role);
    }

    @Transactional
    public void approveUser(Long memberId) {
        Member member = memberGetService.getMember(memberId);
        memberService.approveMember(member);
        memberPatchService.grantInitScore(member);
    }
}
