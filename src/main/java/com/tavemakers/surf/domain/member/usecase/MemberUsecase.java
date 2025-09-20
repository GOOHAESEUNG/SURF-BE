package com.tavemakers.surf.domain.member.usecase;

import com.tavemakers.surf.domain.member.dto.MemberSearchResDTO;
import com.tavemakers.surf.domain.member.entity.Member;
import com.tavemakers.surf.domain.member.entity.Track;
import com.tavemakers.surf.domain.member.service.MemberGetService;
import com.tavemakers.surf.domain.member.service.TrackGetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class MemberUsecase {

    private final MemberGetService memberGetService;
    private final TrackGetService trackGetService;


    //이름으로 유저 찾기
    public MemberSearchResDTO findMemberByNameAndTrack(String name) {
       Member member =  memberGetService.getMemberByName(name);
       Track track = trackGetService.getTrack(member.getId());

       return MemberSearchResDTO.of(member,track.getGeneration(), track.getPart().toString());
    }
}
