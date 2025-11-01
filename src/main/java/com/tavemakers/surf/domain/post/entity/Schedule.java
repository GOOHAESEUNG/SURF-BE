package com.tavemakers.surf.domain.post.entity;

import com.tavemakers.surf.domain.post.dto.req.ScheduleCreateReqDTO;
import com.tavemakers.surf.global.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Schedule extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id")
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private LocalDateTime startAt;

    @Column(nullable = false)
    private LocalDateTime endAt;

    @Column(nullable = false)
    private String location;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    public static Schedule of(ScheduleCreateReqDTO dto) {
        return Schedule.builder()
                .title(dto.title())
                .content(dto.content())
                .startAt(dto.startAt())
                .endAt(dto.endAt())
                .location(dto.location())
                .build();
    }

    public static Schedule of(ScheduleCreateReqDTO dto, Post post) {
        return Schedule.builder()
                .title(dto.title())
                .content(dto.content())
                .startAt(dto.startAt())
                .endAt(dto.endAt())
                .location(dto.location())
                .post(post)
                .build();
    }
}

