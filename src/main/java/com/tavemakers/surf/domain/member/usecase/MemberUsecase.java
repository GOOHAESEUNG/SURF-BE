package com.tavemakers.surf.domain.member.usecase;

import com.tavemakers.surf.domain.member.dto.request.MemberSignupReqDTO;
import com.tavemakers.surf.domain.member.dto.response.*;
import com.tavemakers.surf.domain.member.dto.request.ProfileUpdateReqDTO;
import com.tavemakers.surf.domain.member.dto.response.MyPageProfileResDTO;
import com.tavemakers.surf.domain.member.dto.response.TrackResDTO;
import com.tavemakers.surf.domain.member.entity.Member;
import com.tavemakers.surf.domain.member.entity.Track;
import com.tavemakers.surf.domain.member.exception.TrackNotFoundException;
import com.tavemakers.surf.domain.member.service.*;
import com.tavemakers.surf.domain.score.service.PersonalScoreGetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class MemberUsecase {

    private final MemberGetService memberGetService;
    private final TrackGetService trackGetService;
    private final PersonalScoreGetService personalScoreGetService;
    private final CareerPostService careerPostService;
    private final CareerPatchService careerPatchService;
    private final CareerDeleteService careerDeleteService;
    private final MemberPatchService memberPatchService;
    private final MemberUpsertService memberUpsertService;
    private final MemberServiceImpl memberServiceImpl;
    private final MemberService memberService;


    public MyPageProfileResDTO getMyPageAndProfile(Long memberId) {
        Member member = memberGetService.getMemberByApprovedStatus(memberId);
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
        List<Member> members = memberGetService.getMemberByName(name);
        if (members.isEmpty()) {
            return List.of();
        }

        List<Long> memberIds = members.stream().map(Member::getId).collect(Collectors.toList());

       List<Track> latestTracks = trackGetService.getTrack(memberIds);

        // 조회된 최신 트랙들을 Member ID를 Key로 하는 Map으로 변환
        Map<Long, Track> trackMap = latestTracks.stream()
                .collect(Collectors.toMap(track -> track.getMember().getId(), track -> track));

        List<MemberSearchResDTO> result = new ArrayList<>();
        for (Member member : members) {
            Track latestTrack = trackMap.get(member.getId());

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

    //프로필 수정
    @Transactional
    public void updateProfile(Long memberId, ProfileUpdateReqDTO dto) {
        Member member = memberGetService.getMember(memberId);
        log.info(memberId.toString());

        memberPatchService.updateProfile(member, dto);

        if (dto.getCareersToUpdate() != null) {
            careerPatchService.updateCareer(member, dto.getCareersToUpdate());
        }

        if (dto.getCareerIdsToDelete() != null) {
            careerDeleteService.deleteCareer(member, dto.getCareerIdsToDelete());
        }

        if (dto.getCareersToCreate() != null) {
            careerPostService.createCareer(member, dto.getCareersToCreate());
        }
    }

    //온보딩 필요 여부 확인
    @Transactional
    public Boolean needsOnboarding(Long memberId) {
        Member member = memberGetService.getMember(memberId);
        return memberServiceImpl.needsOnboarding(member);
    }

    //회원가입
    public MemberSignupResDTO signup(Long memberId, MemberSignupReqDTO request) {
        Member member = memberGetService.getMember(memberId);
        return memberService.signup(member, request);
    }
}
