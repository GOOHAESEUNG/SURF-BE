package com.tavemakers.surf.domain.member.service;

import com.tavemakers.surf.domain.login.kakao.dto.KakaoUserInfoDto;
import com.tavemakers.surf.domain.member.entity.Member;
import com.tavemakers.surf.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberUpsertService {

    private final MemberRepository memberRepository;

    @Transactional
    public Member upsertRegisteringFromKakao(KakaoUserInfoDto info) {
        return memberRepository.findByKakaoId(info.id()).orElseGet(() -> {
            Member toSave = Member.createRegisteringFromKakao(info);
            try {
                return memberRepository.saveAndFlush(toSave);
            } catch (org.springframework.dao.DataIntegrityViolationException e) {
                return memberRepository.findByKakaoId(info.id())
                        .orElseThrow(() -> e);
            }
        });
    }
}