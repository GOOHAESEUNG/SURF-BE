package com.tavemakers.surf.domain.member.service;

import com.tavemakers.surf.domain.member.dto.request.ProfileUpdateReqDTO;
import com.tavemakers.surf.domain.member.entity.Member;
import com.tavemakers.surf.domain.member.entity.enums.MemberRole;
import com.tavemakers.surf.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberPatchService {

    @Transactional
    public void updateProfile(Member member, ProfileUpdateReqDTO dto) {
        member.updateProfile(dto);
    }

    @Transactional
    public void grantRole(Member member, MemberRole role) {
        //유저 권한 부여
        member.exchangeRole(role);
    }
}
