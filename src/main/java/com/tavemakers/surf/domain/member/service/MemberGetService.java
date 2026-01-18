package com.tavemakers.surf.domain.member.service;

import com.tavemakers.surf.domain.member.entity.Member;
import com.tavemakers.surf.domain.member.entity.enums.MemberStatus;
import com.tavemakers.surf.domain.member.entity.enums.Part;
import com.tavemakers.surf.domain.member.exception.MemberNotFoundException;
import com.tavemakers.surf.domain.member.repository.MemberRepository;
import com.tavemakers.surf.domain.member.repository.MemberSearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class MemberGetService {

    private final MemberRepository memberRepository;
    private final MemberSearchRepository memberSearchRepository;

    public Member getMemberByStatus(Long memberId, MemberStatus memberStatus) {
        return memberRepository.findByIdAndStatus(memberId, memberStatus)
                .orElseThrow(MemberNotFoundException::new);
    }

    public Member getMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);
    }

    public Member getMemberByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(MemberNotFoundException::new);
    }

    //회원 조회 - 이름 기반 - ID 리스트 반환
    public List<Member> getMemberByName(String name) {
        return memberRepository.findByActivityStatusAndNameAndStatusNot(true, name, MemberStatus.WITHDRAWN);
    }

    public Member readMemberInformation(Long memberId) {
        return memberRepository.findByIdWithTracks(memberId)
                .orElseThrow(MemberNotFoundException::new);
    }

    public Void validateMember(Long memberId) {
        memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);
        return null;
    }

    public Slice<Member> searchMembers(Integer generation, Part part, String keyword, Pageable pageable) {
        return memberSearchRepository.searchMembers(generation, part, keyword, pageable);
    }

    public Long countSearchingMembers(Integer generation, Part memberPart, String keyword) {
        return memberSearchRepository.countMembers(generation, memberPart, keyword);
    }
}
