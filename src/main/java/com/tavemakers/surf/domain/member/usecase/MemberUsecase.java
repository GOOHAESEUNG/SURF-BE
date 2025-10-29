package com.tavemakers.surf.domain.member.usecase;

import com.tavemakers.surf.domain.member.dto.request.CareerCreateReqDTO;
import com.tavemakers.surf.domain.member.dto.request.CareerUpdateReqDTO;
import com.tavemakers.surf.domain.member.dto.request.MemberSignupReqDTO;
import com.tavemakers.surf.domain.member.dto.response.*;
import com.tavemakers.surf.domain.member.dto.request.ProfileUpdateReqDTO;
import com.tavemakers.surf.domain.member.dto.response.MyPageProfileResDTO;
import com.tavemakers.surf.domain.member.dto.response.TrackResDTO;
import com.tavemakers.surf.domain.member.entity.Member;
import com.tavemakers.surf.domain.member.entity.Track;
import com.tavemakers.surf.domain.member.entity.enums.MemberStatus;
import com.tavemakers.surf.domain.member.exception.TrackNotFoundException;
import com.tavemakers.surf.domain.member.service.*;
import com.tavemakers.surf.domain.score.service.PersonalScoreGetService;
import com.tavemakers.surf.global.logging.LogEvent;
import com.tavemakers.surf.global.logging.LogParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
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
    private final CareerGetService careerGetService;
    private final MemberPatchService memberPatchService;
    private final MemberService memberService;
    private final ApplicationContext context;

    /** 마이페이지 + 프로필 조회 */
    public MyPageProfileResDTO getMyPageAndProfile(Long targetId) {
        Member member = memberGetService.getMemberByStatus(targetId,MemberStatus.APPROVED);
        List<TrackResDTO> myTracks = getMyTracks(targetId);
        List<CareerResDTO> myCareers = getMyCareers(targetId);

        if (member.isNotOwner()) { // SURF Rule - 타인의 활동점수는 조회 불가
            return MyPageProfileResDTO.of(member, myTracks, null, myCareers);
        }

        BigDecimal score = null;
        if (member.isActive()) { // SURF Rule - 활동 중인 회원만 활동점수를 보여준다.
            score = personalScoreGetService.getPersonalScore(targetId).getScore();
        }

        return MyPageProfileResDTO.of(member, myTracks, score, myCareers);
    }


    /** 이름으로 회원 검색 후 각 회원의 트랙 정보를 DTO로 반환하는 메소드 **/
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

    /** 트랙+기수별 회원 묶기 */
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
    @LogEvent(value = "member.profile_update", message = "회원 정보 수정")
    @Transactional
    public void updateProfile(@LogParam("member_id") Long memberId,
                              ProfileUpdateReqDTO dto) {

        Member member = memberGetService.getMember(memberId);

        // 프로필 정보 수정
        memberPatchService.updateProfile(member, dto);

        // 경력 수정
        if (dto.careersToUpdate() != null) {
            careerPatchService.updateCareer(member, dto.careersToUpdate());
        }

        // 경력 삭제
        if (dto.careerIdsToDelete() != null) {
            careerDeleteService.deleteCareer(member, dto.careerIdsToDelete());
        }

        // 경력 생성
        if (dto.careersToCreate() != null) {
            careerPostService.createCareer(member, dto.careersToCreate());
        }
    }

    /** 온보딩 필요 여부 확인 */
    @Transactional(readOnly = true)
    public OnboardingCheckResDTO needsOnboarding(
            Long memberId
    ) {
        Member member = memberGetService.getMember(memberId);

        Boolean needOnboarding = memberService.needsOnboarding(member);
        MemberStatus memberStatus = memberService.memberStatusCheck(member);

        OnboardingCheckResDTO dto = OnboardingCheckResDTO.of(memberId, needOnboarding, memberStatus);
        return dto;
    }

    /** 회원가입 요청 및 MemberStatus에 따른 로그 분기 */
    @Transactional
    public MemberSignupResDTO signup(
            Long memberId,
            MemberSignupReqDTO request,
            String requestId
    ) {
        Member member = memberGetService.getMember(memberId);
        MemberStatus status = member.getStatus();

        MemberUsecase proxy = context.getBean(MemberUsecase.class);

        if (status == MemberStatus.APPROVED) {
            MemberSignupResDTO dto = MemberSignupResDTO.from(member);
            return proxy.signupSucceeded(memberId, dto);
        }

        if (status == MemberStatus.REJECTED) {
            int statusCode = 403;
            String errorReason = "ADMIN_REJECTED";
            return proxy.signupFailed(memberId, statusCode, errorReason);
        }

        return proxy.signupCreate(member, request);

    }

    /** 회원가입 create 로그 (온보딩) */
    @Transactional
    @LogEvent(value = "signup.create", message = "회원가입 요청 처리")
    public MemberSignupResDTO signupCreate(Member member, MemberSignupReqDTO request) {
        return memberService.signup(member, request);
    }

    /** 회원가입 성공 */
    @Transactional
    @LogEvent(value = "signup.succeeded", message = "회원가입 성공")
    public MemberSignupResDTO signupSucceeded(
            Long memberId,
            MemberSignupResDTO response
    ) {
        Member member = memberGetService.getMember(memberId);
        return MemberSignupResDTO.from(member);
    }

    /** 회원가입 실패 */
    @Transactional
    @LogEvent(value = "signup.failed", message = "회원가입 실패")
    public MemberSignupResDTO signupFailed(
            Long memberId,
            int statusCode,
            String errorReason
    ) {
        throw new RuntimeException(errorReason);
    }

    private List<CareerResDTO> getMyCareers(Long memberId) {
        return careerGetService.getMyCareers(memberId)
                .stream().map(CareerResDTO::from).toList();
    }

    private List<TrackResDTO> getMyTracks(Long memberId) {
        return trackGetService.getTrack(memberId)
                .stream().map(TrackResDTO::from).toList();
    }
}
