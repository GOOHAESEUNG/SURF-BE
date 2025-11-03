package com.tavemakers.surf.domain.board.entity;

import com.tavemakers.surf.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BoardCategory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    @Column(nullable = false)
    @NotBlank
    private String name;          // 예: 행사, 활동, 제휴, 릴리스, 패치, 기타

    @Column(nullable = false, unique = true)
    @NotBlank
    private String slug;          // URL/식별자 (board 내 unique)

    @Builder
    private BoardCategory(
            Board board,
            String name,
            String slug) {
        this.board = board;
        this.name = name;
        this.slug = slug;
    }

    public void update(String name, String slug) {
        if (name != null) this.name = name;
        if (slug != null) this.slug = slug;
    }
}