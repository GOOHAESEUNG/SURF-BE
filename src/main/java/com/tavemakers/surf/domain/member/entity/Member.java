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
import com.tavemakers.surf.domain.member.entity.enums.Part;

import java.util.ArrayList;
import java.util.List;

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

    private String university;

    private String graduateSchool;

    @Column(nullable = false, unique = true) // 이메일은 고유해야 함
    private String email;

    private String phoneNumber;

    private Integer activityScore;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Track> tracks = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberStatus status = MemberStatus.WAITING; // 회원 상태; // 회원 상태 (가입중, 대기중, 승인)

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
        this.status = status != null ? status : MemberStatus.WAITING;
        this.role = role != null ? role : MemberRole.MEMBER;
        this.memberType = memberType != null ? memberType : MemberType.YB;
        this.activityStatus = activityStatus;
        this.activityScore = 0;
        this.tracks = new ArrayList<>();
    }

    /** ===== [정적 팩토리 메서드] ===== */
    public static Member create(MemberSignupReqDTO request,
                                String normalizedEmail,
                                String normalizedPhone) {
        Member member = Member.builder()
                .name(request.getName())
                .university(request.getUniversity())
                .graduateSchool(request.getGraduateSchool())
                .email(normalizedEmail)
                .phoneNumber(normalizedPhone)
                .profileImageUrl(request.getProfileImageUrl())
                .status(MemberStatus.WAITING)
                .role(MemberRole.MEMBER)
                .memberType(MemberType.YB)
                .activityStatus(true)
                .build();

        // DTO의 TrackInfo → Track 엔티티 변환
        if (request.getTracks() != null) {
            request.getTracks().forEach(t ->
                    member.addTrack(t.getGeneration(), t.getPart())
            );
        }

        return member;
    }

    public static Member createRegisteringFromKakao(KakaoUserInfoDto info) {
        var acc = info.kakaoAccount();

        if (acc == null || acc.email() == null || acc.email().isBlank()) {
            throw new IllegalStateException("카카오 계정 이메일 권한이 필요합니다.");
        }

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

    public void applySignup(MemberSignupReqDTO req, String normalizedPhone) {
        this.name = req.getName();
        this.university = req.getUniversity();
        this.graduateSchool = req.getGraduateSchool();
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

    /** ===== [도메인 행위 메서드] ===== */
    public void approve() {
        this.status = MemberStatus.APPROVED;
    }

    public void reject() {
        this.status = MemberStatus.REJECTED;
    }

    /** ===== [연관관계 편의 메서드] ===== */
    // 트랙 추가 (기수+파트로 생성)
    public void addTrack(Integer generation, Part part) {
        boolean exists = this.tracks.stream()
                .anyMatch(t -> t.getGeneration().equals(generation));

        if (exists) return; // 같은 기수 이미 있으면 추가 안 함

        Track track = new Track(generation, part);
        track.setMember(this); // 여기서만 add 수행
    }
}
