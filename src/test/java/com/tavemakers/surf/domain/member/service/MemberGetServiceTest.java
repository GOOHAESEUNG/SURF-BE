package com.tavemakers.surf.domain.member.service;

import com.tavemakers.surf.domain.member.dto.MemberSearchResDTO;
import com.tavemakers.surf.domain.member.entity.Member;
import com.tavemakers.surf.domain.member.entity.Track;
import com.tavemakers.surf.domain.member.entity.enums.Part;
import com.tavemakers.surf.domain.member.exception.MemberNotFoundException;
import com.tavemakers.surf.domain.member.repository.MemberRepository;
import com.tavemakers.surf.domain.member.repository.TrackRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class MemberGetServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private TrackRepository trackRepository;

    @InjectMocks
    private MemberGetService memberGetService;

    // 테스트용 Member 객체를 생성하는 팩토리 메소드
    private static Member createTestMember(Long id, String name, boolean activityStatus) {
        // 실제 코드의 Member 엔티티에 @Builder나 테스트용 생성자가 있어야 합니다.
        return Member.createMemberforTest(name, "test@email.com", "010-1234-5678", "테스트대학교", activityStatus);
    }

    // 테스트용 Track 객체를 생성하는 팩토리 메소드
    private static Track createTestTrack(Long id, int generation, Part part, Member member) {
        // 실제 코드의 Track 엔티티에 @Builder나 테스트용 생성자가 있어야 합니다.
        return Track.createTrackForTest(id, generation, part, member);
    }



    @Test
    @DisplayName("회원 이름으로 조회 성공 - DTO 반환")
    void getMemberByName_Success_ReturnsDTO() {
        // given
        String memberName = "김서핑";
        Long memberId = 1L;
        int generation = 10;
        Part part = Part.BACKEND;

        // Mock 객체의 동작 설정: findByNameAndActivityStatus()가 Member 객체를 반환하도록 설정
        Member foundMember = createTestMember(memberId, memberName, true);
        given(memberRepository.findByActivityStatusAndName(true, memberName))
                .willReturn(Optional.of(foundMember));

        // Mock 객체의 동작 설정: findByMemberId()가 Track 객체를 반환하도록 설정
        Track foundTrack = createTestTrack(1L, generation, part, foundMember);
        given(trackRepository.findByMemberId(memberId))
                .willReturn(foundTrack);

        // when
        MemberSearchResDTO resDTO = memberGetService.getMemberByName(memberName);

        // then
        assertNotNull(resDTO);
        assertEquals(memberName, resDTO.getName());
        assertEquals(generation, resDTO.getGeneration());
        assertEquals(part.toString(), resDTO.getTrack());
    }



    @Test
    @DisplayName("존재하지 않는 회원 이름으로 조회 시 MemberNotFoundException 발생")
    void getMemberByName_NotFound_ThrowsException() {
        // given
        String nonExistentName = "없는회원";
        given(memberRepository.findByActivityStatusAndName(true, nonExistentName))
                .willReturn(Optional.empty());

        // when & then
        assertThrows(MemberNotFoundException.class, () -> {
            memberGetService.getMemberByName(nonExistentName);
        });
    }
}