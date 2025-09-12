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
public class Career extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "career_id") // ERD의 '경력_id'에 맞춰 컬럼명 지정
    private Long id;

    @Column(nullable = false)
    private String companyName; // 회사명

    @Column(nullable = false)
    private String position; // 직무

    @Column(nullable = false)
    private LocalDate startDate; // 근무 시작일

    @Column(nullable = false)
    private LocalDate endDate; // 근무 종료일 (진행 중일 경우 null)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Builder
    public Career(String companyName, String jobTitle, LocalDate startDate, LocalDate endDate, Member member) {
        this.companyName = companyName;
        this.position = jobTitle;
        this.startDate = startDate;
        this.endDate = endDate;
        this.member = member;
    }
}
