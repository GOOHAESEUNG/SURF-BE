package com.tavemakers.surf.domain.member.service;

import com.tavemakers.surf.domain.member.dto.MemberSearchResDTO;
import com.tavemakers.surf.domain.member.entity.Member;
import com.tavemakers.surf.domain.member.entity.Track;
import com.tavemakers.surf.domain.member.entity.enums.MemberStatus;
import com.tavemakers.surf.domain.member.exception.MemberNotFoundException;
import com.tavemakers.surf.domain.member.repository.MemberRepository;
import com.tavemakers.surf.domain.member.repository.TrackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberGetService {

    private final MemberRepository memberRepository;
    private final TrackRepository trackRepository;

    public Member getMember(Long memberId) {
        return memberRepository.findByIdAndStatus(memberId, MemberStatus.APPROVED)
                .orElseThrow(MemberNotFoundException::new);
    }

    //회원 조회 - 이름
    public MemberSearchResDTO getMemberByName(String name) {
        Member member =  memberRepository.findByActivityStatusAndName(true,name)
                .orElseThrow(MemberNotFoundException::new); //검색한 이름의 유저가 없을 경우

        Track track = trackRepository.findByMemberId(member.getId());

        return MemberSearchResDTO.of(member, track.getGeneration(), track.getPart().toString());
    }


}
