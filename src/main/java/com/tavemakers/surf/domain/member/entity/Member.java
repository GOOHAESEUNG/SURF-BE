package com.tavemakers.surf.domain.member.entity;

import com.tavemakers.surf.domain.login.kakao.dto.KakaoUserInfoDto;
import com.tavemakers.surf.domain.member.dto.request.ProfileUpdateReqDTO;
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
import lombok.extern.slf4j.Slf4j;

@Entity
@Getter
@Slf4j
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 기본 생성자 protected 설정
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    private Long kakaoId;

    @Column(nullable = false)
    private String name;

    private String profileImageUrl;

    private String university;

    private String graduateSchool;

    @Column(nullable = false, unique = true) // 이메일은 고유해야 함
    private String email;

    private String phoneNumber;

    private Integer activityScore;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberStatus status; // 회원 상태 (가입중, 대기중, 승인)

    @Enumerated(EnumType.STRING)
    private MemberRole role; // 역할 (루트, 회장, 매니저, 회원)

    @Enumerated(EnumType.STRING)
    private MemberType memberType; // OB, YB 구분

    private boolean activityStatus; // 활동/비활동 여부

    public boolean isYB() {
        return memberType == MemberType.YB;
    }

    public boolean isActive() {
        return activityStatus;
    }

    @Builder
    public Member(Long kakaoId,
                  String name,
                  String profileImageUrl,
                  String university,
                  String graduateSchool,
                  String email,
                  String phoneNumber,
                  MemberStatus status,
                  MemberRole role,
                  MemberType memberType,
                  boolean activityStatus) {
        this.kakaoId = kakaoId;
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

        if (acc == null || acc.email() == null || acc.email().isBlank()) {
            throw new IllegalStateException("카카오 계정 이메일 권한이 필요합니다.");
        }

        return Member.builder()
                .kakaoId(info.id())
                .name(acc.profile().nickname())
                .email(acc.email())
                .profileImageUrl(acc.profile().profileImageUrl())
                .status(MemberStatus.REGISTERING)
                .role(MemberRole.MEMBER)
                .memberType(MemberType.YB)
                .activityStatus(true)
                .build();
    }

    public void applySignup(MemberSignupReqDTO req,String normalizedEmail, String normalizedPhone) {
        this.name = req.getName();
        this.university = req.getUniversity();
        this.graduateSchool = req.getGraduateSchool();
        this.email = normalizedEmail;
        this.phoneNumber = normalizedPhone;

        // 기본 정책 보정 (비어있을 수 있는 값들)
        if (this.role == null) this.role = MemberRole.MEMBER;
        if (this.memberType == null) this.memberType = MemberType.YB;
        this.activityStatus = true;

        // 상태 전이: REGISTERING -> WAITING (또는 정책상 APPROVED)
        if (this.status == MemberStatus.REGISTERING) {
            this.status = MemberStatus.WAITING;
        }
    }

    //프로필 수정하기
    public void updateProfile(ProfileUpdateReqDTO request) {
        if (request.getPhoneNumber() != null) {
            this.phoneNumber = request.getPhoneNumber();
        }
        if (request.getEmail() != null) {
            // 이메일은 중복 체크 등 추가 로직이 필요할 수 있음
            this.email = request.getEmail();
        }
        if (request.getUniversity() != null) {
            this.university = request.getUniversity();
        }
        if (request.getGraduateSchool() != null) {
            this.graduateSchool = request.getGraduateSchool();
        }
    }
}
