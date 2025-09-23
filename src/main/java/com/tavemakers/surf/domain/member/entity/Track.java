package com.tavemakers.surf.domain.member.entity;

import com.tavemakers.surf.global.common.entity.BaseEntity;
import com.tavemakers.surf.domain.member.entity.enums.Part;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Track extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "track_id")
    private Long id;

    @Column(nullable = false)
    private Integer generation; // 기수

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Part part; // 파트 (e.g., BACKEND, FRONTEND)

    @ManyToOne(fetch = FetchType.LAZY) // 다대일 관계 설정
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    public Track(Integer generation, Part part, Member member) {
        this.generation = generation;
        this.part = part;
        this.member = member;
    }

}
