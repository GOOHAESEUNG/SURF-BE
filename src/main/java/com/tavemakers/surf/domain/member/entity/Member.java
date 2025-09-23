package com.tavemakers.surf.domain.member.entity;

import com.tavemakers.surf.domain.login.kakao.dto.KakaoUserInfoDto;
import com.tavemakers.surf.global.common.entity.BaseEntity;
import com.tavemakers.surf.domain.member.dto.request.MemberSignupReqDTO;
import com.tavemakers.surf.domain.member.entity.enums.MemberType;
import com.tavemakers.surf.domain.member.entity.enums.MemberRole;
import com.tavemakers.surf.domain.member.entity.enums.MemberStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Builder;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 기본 생성자 protected 설정
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    /** ===== [회원가입 시 입력 받는 필드] ===== */
    @Column(nullable = false)
    private String name;

    private String profileImageUrl;

    private String university;

    private String graduateSchool;

    @Column(nullable = false, unique = true) // 이메일은 고유해야 함
    private String email;

    private String phoneNumber;

    /** ===== [시스템/운영자가 관리하는 필드] ===== */
    private int activityScore;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberStatus status; // 회원 상태 (가입중, 대기중, 승인)

    private String kakaoId; // 카카오 ID는 보통 문자열로 관리

    @Enumerated(EnumType.STRING)
    private MemberRole role; // 역할 (루트, 회장, 매니저, 회원)

    @Enumerated(EnumType.STRING)
    private MemberType memberType; // OB, YB 구분

    private boolean activityStatus; // 활동/비활동 여부

    /** ===== [도메인 로직 메서드] ===== */
    public boolean isYB() {
        return memberType == MemberType.YB;
    }

    @Builder
    public Member(String name,
                  String profileImageUrl,
                  String university,
                  String graduateSchool,
                  String email,
                  String phoneNumber,
                  MemberStatus status,
                  MemberRole role,
                  MemberType memberType,
                  boolean activityStatus) {
        this.name = name;
        this.profileImageUrl = profileImageUrl;
        this.university = university;
        this.graduateSchool = graduateSchool;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.status = status;
        this.role = role;
        this.memberType = memberType;
        this.activityStatus = activityStatus;
        this.activityScore = 0; // 기본값
    }

    /** ===== [정적 팩토리 메서드] ===== */
    public static Member create(MemberSignupReqDTO request,
                                String normalizedEmail,
                                String normalizedPhone) {
        return Member.builder()
                .name(request.getName())
                .university(request.getUniversity())
                .graduateSchool(request.getGraduateSchool())
                .email(normalizedEmail)
                .phoneNumber(normalizedPhone)
                .profileImageUrl(request.getProfileImageUrl())
                .status(MemberStatus.WAITING)   // 기본값
                .role(MemberRole.MEMBER)        // 기본값
                .memberType(MemberType.YB)      // 예시: 회원가입 시 기본 YB
                .activityStatus(true)           // 기본값
                .build();
    }

    public static Member createRegisteringFromKakao(KakaoUserInfoDto info) {
        var acc = info.kakaoAccount();
        return Member.builder()
                .name(acc.profile().nickname())
                .email(acc.email())
                .profileImageUrl(acc.profile().profileImageUrl())
                .status(MemberStatus.REGISTERING)
                .role(MemberRole.MEMBER)
                .memberType(MemberType.YB)
                .activityStatus(true)
                .build();
    }
}
