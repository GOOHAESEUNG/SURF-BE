package com.tavemakers.surf.domain.badge.entity;

import com.tavemakers.surf.domain.badge.dto.request.MemberBadgeReqDTO;
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
public class MemberBadge extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_badge_id")
    private Long id;

    private Long memberId;

    private LocalDate awardedAt; //수여일자

    private Integer generation;

    private String badgeName;

    public static MemberBadge of(MemberBadgeReqDTO dto, Long memberId) {
        return MemberBadge.builder()
                .badgeName(dto.badgeType().getDisplayName())
                .generation(dto.generation())
                .awardedAt(dto.awardedAt())
                .memberId(memberId).build();
    }

}
