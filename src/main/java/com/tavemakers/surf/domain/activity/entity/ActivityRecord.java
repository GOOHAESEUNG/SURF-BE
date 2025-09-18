package com.tavemakers.surf.domain.activity.entity;

import com.tavemakers.surf.domain.activity.entity.enums.ActivityCategory;
import com.tavemakers.surf.domain.activity.entity.enums.ActivityType;
import com.tavemakers.surf.domain.activity.entity.enums.ScoreType;
import com.tavemakers.surf.domain.member.entity.Member;
import com.tavemakers.surf.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Entity
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ActivityRecord extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Enumerated(EnumType.STRING)
    private ActivityCategory category; // 대주제

    @Enumerated(EnumType.STRING)
    private ActivityType activityType; // 소주제 (활동 기록 명)

    @Enumerated(EnumType.STRING)
    private ScoreType scoreType; // 상점, 벌점 여부

    private LocalDate activityDate;

    private Long prefixSum; // 누적합

    private Long appliedScore; // 적용 점수

    @Column(nullable = false, columnDefinition = "TINYINT(1) default 0")
    private boolean isDeleted = false;

    // TODO 정적 팩토리 메서드

}
