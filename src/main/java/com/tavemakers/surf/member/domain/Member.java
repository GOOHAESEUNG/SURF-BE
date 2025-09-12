package com.tavemakers.surf.member.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 기본 생성자 protected 설정
public class Member {

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
    private MemberClassification memberType; // OB, YB 구분

    private boolean isActive; // 활동/비활동 여부

    @Builder
    public Member(String name, String profileImageUrl, String university, String graduateSchool,
                  String email, String phoneNumber, Integer activityScore, MemberStatus status,
                  String kakaoId, MemberRole role, MemberClassification memberType, boolean isActive) {
        this.name = name;
        this.profileImageUrl = profileImageUrl;
        this.university = university;
        this.graduateSchool = graduateSchool;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.activityScore = activityScore;
        this.status = status;
        this.kakaoId = kakaoId;
        this.role = role;
        this.memberType = memberType;
        this.isActive = isActive;
    }
}
