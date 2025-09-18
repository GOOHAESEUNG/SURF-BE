package com.tavemakers.surf.domain.score.entity;

import com.tavemakers.surf.domain.member.entity.Member;
import com.tavemakers.surf.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PersonalActivityScore extends BaseEntity implements ScoreComputable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(nullable = false)
    private double score;

    @Override
    public double getScore() {
        return this.score;
    }

    @Override
    public double updateScore(double score) {
        this.score += score;
        return this.score;
    }

    public static PersonalActivityScore create(Member member) {
        return PersonalActivityScore.builder()
                .member(member)
                .score(member.isYB() ? 100 : 50) // 기본 점수 100
                .build();
    }

}
