package com.tavemakers.surf.domain.member.service;

import com.tavemakers.surf.domain.login.auth.service.RefreshTokenService;
import com.tavemakers.surf.domain.member.entity.Member;
import com.tavemakers.surf.domain.member.entity.enums.MemberStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberWithdrawService {

    private final MemberGetService memberGetService;
    private final RefreshTokenService refreshTokenService;

    @Transactional
    public void withdraw(Long memberId) {
        Member member = memberGetService.getMember(memberId);

        refreshTokenService.invalidateAll(memberId);

        if (member.getStatus() != MemberStatus.WITHDRAWN) {
            member.withdraw();
        }
    }
}