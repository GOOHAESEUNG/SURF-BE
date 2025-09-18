package com.tavemakers.surf.domain.activity.entity;

import com.tavemakers.surf.domain.activity.dto.request.ActivityRecordReqDTO;
import com.tavemakers.surf.domain.activity.entity.enums.ActivityCategory;
import com.tavemakers.surf.domain.activity.entity.enums.ActivityType;
import com.tavemakers.surf.domain.activity.entity.enums.ScoreType;
import com.tavemakers.surf.global.common.entity.BaseEntity;
import io.hypersistence.utils.hibernate.id.Tsid;
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
    @Tsid
    private Long id;

    private Long memberId;

    @Enumerated(EnumType.STRING)
    private ActivityCategory category; // 대주제

    @Enumerated(EnumType.STRING)
    private ActivityType activityType; // 소주제 (활동 기록 명)

    @Enumerated(EnumType.STRING)
    private ScoreType scoreType; // 상점, 벌점 여부

    private LocalDate activityDate;

    private double prefixSum; // 누적합

    private double appliedScore;

    @Column(nullable = false, columnDefinition = "TINYINT(1) default 0")
    private boolean isDeleted = false;

    // TODO 정적 팩토리 메서드
    public static ActivityRecord of(Long memberId, ActivityRecordReqDTO dto, double prefixSum) {

        ActivityType type = ActivityType.valueOf(dto.activityName());

        return ActivityRecord.builder()
                .memberId(memberId)
                .category(dto.category() != null ? ActivityCategory.valueOf(dto.category()) : null)
                .activityType(type)
                .activityDate(dto.activityDate())
                .scoreType(type.getScoreType())
                .appliedScore(type.getDelta())
                .prefixSum(prefixSum)
                .isDeleted(false)
                .build();
    }

}
