package com.tavemakers.surf.domain.member.service;

import com.tavemakers.surf.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberPatchService {

    private final MemberRepository memberRepository;



}
