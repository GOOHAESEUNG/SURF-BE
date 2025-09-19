package com.tavemakers.surf.domain.board.entity;

import com.tavemakers.surf.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Board extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull
    private BoardType type;

    @Builder
    private Board(BoardType type) {
        this.type = type;
    }

    public static Board of(BoardType type) {
        return Board.builder()
                .type(type)
                .build();
    }

    public void changeType(BoardType type) {
        this.type = type;
    }
}
