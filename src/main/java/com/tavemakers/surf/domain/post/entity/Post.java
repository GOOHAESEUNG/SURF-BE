package com.tavemakers.surf.domain.post.entity;

import com.tavemakers.surf.domain.board.entity.Board;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post {
    @Id @GeneratedValue
    private Long id;

    @NotBlank
    private String title;

    @NotBlank
    private String content;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Board board;

    private LocalDateTime postedAt;

    private boolean pinned; // 상단 고정
}