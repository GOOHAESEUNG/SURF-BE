package com.tavemakers.surf.domain.member.entity;

import com.tavemakers.surf.domain.member.dto.request.CareerCreateReqDTO;
import com.tavemakers.surf.domain.member.dto.request.CareerUpdateReqDTO;
import com.tavemakers.surf.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Entity
@Getter
@SuperBuilder
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

    private LocalDate endDate; // 근무 종료일 (진행 중일 경우 null)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @NotNull
    private boolean isWorking;

    //경력 수정
    public void update(CareerUpdateReqDTO dto){
        if(dto.getCompanyName() != null){
            this.companyName = dto.getCompanyName();
        }
        if(dto.getPosition() != null){
            this.position = dto.getPosition();
        }
        if(dto.getStartDate() != null){
            this.startDate = dto.getStartDate().atDay(1);
        }
        if(dto.getEndDate() != null){
            this.endDate = dto.getEndDate().atDay(1);
        }
    }

    //정적 팩토리 메소드 - 생성
    public static Career of(CareerCreateReqDTO dto, Member member) {
        return Career.builder()
                .companyName(dto.getCompanyName())
                .position(dto.getPosition())
                .startDate(dto.getStartDate().atDay(1))
                .endDate(dto.getEndDate() != null ? dto.getEndDate().atDay(1) : null)
                .member(member)
                .isWorking(dto.isWorking())
                .build();
    }

}
