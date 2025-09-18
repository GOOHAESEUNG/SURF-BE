package com.tavemakers.surf.domain.member.entity;

import com.tavemakers.surf.global.common.entity.BaseEntity;
import com.tavemakers.surf.domain.member.entity.enums.MemberType;
import com.tavemakers.surf.domain.member.entity.enums.MemberRole;
import com.tavemakers.surf.domain.member.entity.enums.MemberStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 기본 생성자 protected 설정
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    private String profileImageUrl;

    @Column(nullable = false)
    private String university;

    private String graduateSchool;

    @Column(nullable = false, unique = true) // 이메일은 고유해야 함
    private String email;

    @Column(nullable = false)
    private String phoneNumber;

    private Integer activityScore;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberStatus status; // 회원 상태 (가입중, 대기중, 승인)

    private String kakaoId; // 카카오 ID는 보통 문자열로 관리

    @Enumerated(EnumType.STRING)
    private MemberRole role; // 역할 (루트, 회장, 매니저, 회원)

    @Enumerated(EnumType.STRING)
    private MemberType memberType; // OB, YB 구분

    private boolean activityStatus; // 활동/비활동 여부

    public boolean isYB() {
        return memberType == MemberType.YB;
    }

    public static Member createMemberforTest(String name, String email, String phoneNumber, String university, Boolean activityStatus) {
        Member member = new Member();
        member.name = name;
        member.email = email;
        member.phoneNumber = phoneNumber;
        member.university = university;
        member.activityStatus = activityStatus;
        member.status = MemberStatus.REGISTERING; // 기본 상태 설정
        member.role = MemberRole.MEMBER; // 기본 역할 설정
        return member;
    }

}
