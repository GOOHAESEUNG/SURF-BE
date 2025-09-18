package com.tavemakers.surf.domain.member.service;

import com.tavemakers.surf.domain.member.entity.Member;
import com.tavemakers.surf.domain.member.repository.MemberRepository;
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

    @InjectMocks
    private MemberGetService memberGetService;

    // 테스트를 위한 정적 팩토리 메서드
    private static Member createTestMember(String name, boolean activityStatus) {
        // 엔티티의 정적 팩토리 메서드를 사용하되, 테스트에 필요한 값만 전달
        // 이메일, 전화번호, 대학 등은 임의의 값으로 설정
        return Member.createMemberforTest(name,
                name + "@test.com",
                "010-1234-5678",
                "테스트대학교",
                activityStatus);
    }

    @Test
    @DisplayName("회원 이름으로 조회 성공 - 활동 중인 회원")
    void getMemberByName_Success_ActiveMember() {
        // given
        String memberName = "김서핑";
        Member activeMember = createTestMember(memberName, true);
        given(memberRepository.findByNameAndActivityStatus(true,memberName))
                .willReturn(Optional.of(activeMember));

        // when
        Member foundMember = memberGetService.getMemberByName(memberName);

        // then
        assertNotNull(foundMember);
        assertEquals(memberName, foundMember.getName());
        assertTrue(foundMember.isActivityStatus());
    }

    @Test
    @DisplayName("존재하지 않는 회원 이름으로 조회 시 null 반환")
    void getMemberByName_NotFound_ReturnsNull() {
        // given
        String nonExistentName = "없는회원";
        given(memberRepository.findByNameAndActivityStatus(true,nonExistentName))
                .willReturn(Optional.empty());

        // when
        Member foundMember = memberGetService.getMemberByName(nonExistentName);

        // then
        assertNull(foundMember);
    }

    // 추가 테스트: 여러 명의 회원을 조회하는 시나리오
    @Test
    @DisplayName("여러 명의 회원 생성 및 조회 테스트")
    void createAndFindMultipleMembers() {
        // given
        Member member1 = createTestMember("유저A", true);
        Member member2 = createTestMember("유저B", true);

        // member1을 검색할 때의 Mocking
        given(memberRepository.findByNameAndActivityStatus(true,"유저A" ))
                .willReturn(Optional.of(member1));

        // member2를 검색할 때의 Mocking
        given(memberRepository.findByNameAndActivityStatus(true,"유저B"))
                .willReturn(Optional.of(member2));

        // when
        Member foundUserA = memberGetService.getMemberByName("유저A");
        Member foundUserB = memberGetService.getMemberByName("유저B");

        // then
        assertNotNull(foundUserA);
        assertEquals("유저A", foundUserA.getName());

        assertNotNull(foundUserB);
        assertEquals("유저B", foundUserB.getName());
    }
}