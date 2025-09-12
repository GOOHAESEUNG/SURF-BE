package com.tavemakers.surf.member.domain;

import com.tavemakers.surf.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberBadge extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_badge_id")
    private Long id;

    private LocalDate awardedAt; //수여일자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "badge_name")
    private Badge badge;

    @Builder
    public MemberBadge(LocalDate awardedAt, Member member, Badge badge) {
        this.awardedAt = awardedAt;
        this.member = member;
        this.badge = badge;
    }
}
