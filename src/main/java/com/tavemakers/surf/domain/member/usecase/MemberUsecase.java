package com.tavemakers.surf.domain.member.usecase;

import com.tavemakers.surf.domain.member.dto.MemberSearchResDTO;
import com.tavemakers.surf.domain.member.entity.Member;
import com.tavemakers.surf.domain.member.entity.Track;
import com.tavemakers.surf.domain.member.service.MemberGetService;
import com.tavemakers.surf.domain.member.service.TrackGetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class MemberUsecase {

    private final MemberGetService memberGetService;
    private final TrackGetService trackGetService;


    // 여러 명의 회원을 조회하고, 각 회원의 트랙 정보를 DTO로 반환하는 메소드
    public List<MemberSearchResDTO> findMemberByNameAndTrack(String name) {
        // 1. 이름으로 여러 명의 회원을 조회합니다.
        List<Member> members = memberGetService.getMemberByName(name);

        // 2. 조회된 회원이 없으면 빈 리스트를 반환합니다.
        if (members.isEmpty()) {
            return List.of(); // 또는 return Collections.emptyList();
        }

        // 3. 각 회원을 순회하며 DTO로 변환합니다.
        return members.stream()
                .map(member -> {
                    // 4. 각 회원의 ID로 트랙 정보를 조회합니다.
                    //트랙을 찾지 못할 경우를 대비하여 null 체크를 포함합니다.
                    Track track = trackGetService.getTrack(member.getId());

                    if (track != null) {
                        return MemberSearchResDTO.of(member, track.getGeneration(), track.getPart().toString());
                    } else {
                        // 트랙 정보가 없는 경우, 임시로 널
                        return MemberSearchResDTO.of(member, null, null);
                    }
                })
                .collect(Collectors.toList());
    }
}
