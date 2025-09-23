package com.tavemakers.surf.domain.member.service;

import com.tavemakers.surf.domain.member.dto.request.ProfileUpdateRequestDto;
import com.tavemakers.surf.domain.member.entity.Member;
import com.tavemakers.surf.domain.member.exception.MemberNotFoundException;
import com.tavemakers.surf.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberPatchService {

    private final MemberRepository memberRepository;

    @Transactional
    public void updateProfile(Long memberId, ProfileUpdateRequestDto request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);

        member.updateProfile(request);
    }

}
