package com.tavemakers.surf.domain.member.usecase;

import com.tavemakers.surf.domain.member.entity.Member;
import com.tavemakers.surf.domain.member.entity.enums.MemberRole;
import com.tavemakers.surf.domain.member.entity.enums.MemberStatus;
import com.tavemakers.surf.domain.member.service.MemberGetService;
import com.tavemakers.surf.domain.member.service.MemberPatchService;
import com.tavemakers.surf.domain.member.service.MemberService;
import com.tavemakers.surf.domain.score.service.PersonalScoreSaveService;
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
    private final PersonalScoreSaveService personalScoreSaveService;

    @Transactional
    public void changeRole (Long memberId, MemberRole role) {
        Member member = memberGetService.getMember(memberId);
        memberPatchService.grantRole(member, role);
    }

    @Transactional
    public void approveMember(Long memberId) {
        Member member = memberGetService.getMemberByStatus(memberId, MemberStatus.WAITING);
        memberService.approveMember(member);
        personalScoreSaveService.savePersonalScore(member);
    }

    @Transactional
    public void rejectMember(Long memberId) {
        Member member = memberGetService.getMemberByStatus(memberId, MemberStatus.WAITING);
        memberService.rejectMember(member);
    }
}
