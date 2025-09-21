package com.tavemakers.surf.domain.member.usecase;

import com.tavemakers.surf.domain.activity.service.ActivityRecordGetService;
import com.tavemakers.surf.domain.member.dto.MemberSearchResDTO;
import com.tavemakers.surf.domain.member.dto.MemberSimpleResDTO;
import com.tavemakers.surf.domain.member.dto.response.MyPageProfileResDTO;
import com.tavemakers.surf.domain.member.dto.response.TrackResDTO;
import com.tavemakers.surf.domain.member.entity.Member;
import com.tavemakers.surf.domain.member.entity.Track;
import com.tavemakers.surf.domain.member.exception.TrackNotFoundException;
import com.tavemakers.surf.domain.member.service.MemberGetService;
import com.tavemakers.surf.domain.member.service.TrackGetService;
import com.tavemakers.surf.domain.score.entity.PersonalActivityScore;
import com.tavemakers.surf.domain.score.service.PersonalScoreGetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class MemberUsecase {

    private final MemberGetService memberGetService;
    private final TrackGetService trackGetService;
    private final PersonalScoreGetService personalScoreGetService;

    public MyPageProfileResDTO getMyPageAndProfile(Long memberId) {
        Member member = memberGetService.getMember(memberId);
        List<TrackResDTO> trackList = trackGetService.getTrack(memberId)
                .stream().map(TrackResDTO::from).toList();

        BigDecimal score = null;
        if (member.isActive()) {
            score = personalScoreGetService.getPersonalScore(memberId).getScore();
        }

        return MyPageProfileResDTO.of(member, trackList, score);
    }

    // 여러 명의 회원을 조회하고, 각 회원의 트랙 정보를 DTO로 반환하는 메소드
    public List<MemberSearchResDTO> findMemberByNameAndTrack(String name) {
        // 1. 이름으로 여러 명의 회원을 조회하여 ID 리스트 반환
        List<Member> members = memberGetService.getMemberByName(name);

        // 2. 조회된 회원이 없으면 빈 리스트를 반환합니다.
        if (members.isEmpty()) {
            return List.of();
        }

        //조회된 회원 id 리스트를 생성
        List<Long> memberIds = members.stream().map(Member::getId).collect(Collectors.toList());

        // 3. 회원 id 리스트를 통해 트랙 리스트 가져옴
       List<Track> latestTracks = trackGetService.getTrack(memberIds);

        // 4. 조회된 최신 트랙들을 Member ID를 Key로 하는 Map으로 변환
        Map<Long, Track> trackMap = latestTracks.stream()
                .collect(Collectors.toMap(track -> track.getMember().getId(), track -> track));

        // 5. DB 접근 없이 메모리에서 매핑하여 최종 DTO 생성
        List<MemberSearchResDTO> result = new ArrayList<>();
        for (Member member : members) {
            Track latestTrack = trackMap.get(member.getId());

            // 트랙이 매핑되어있지 않은 멤버가 존재하는 경우 예외처리
            if (latestTrack == null) {
                throw new TrackNotFoundException ("해당 회원의 트랙을 찾을 수 없습니다. 이름/id : " + member.getName() + "/" +member.getId());
            }

            //현재는 기수까지 불러오고 있는데 추후에 기수가 필요하지 않다면 삭제 가능
            result.add(MemberSearchResDTO.of(member, latestTrack.getGeneration(), latestTrack.getPart().toString()));
        }

        return result;
    }

    //트랙+기수별 회원을 묶어 반환
    public Map<String, List<MemberSimpleResDTO>> getMembersGroupedByTrack() {
        return trackGetService.getAllTracksWithMember().stream()
                .collect(Collectors.groupingBy(
                        track -> track.getPart().name() + "_" + track.getGeneration() + "기",
                        Collectors.mapping(
                                track -> MemberSimpleResDTO.from(track.getMember()),
                                Collectors.toList()
                        )
                ));
    }

}
