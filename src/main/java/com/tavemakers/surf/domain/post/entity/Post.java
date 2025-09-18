package com.tavemakers.surf.domain.post.entity;

import com.tavemakers.surf.domain.board.entity.Board;
import com.tavemakers.surf.domain.post.dto.req.PostCreateReqDTO;
import com.tavemakers.surf.domain.post.dto.req.PostUpdateReqDTO;
import com.tavemakers.surf.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends BaseEntity {
    @Id @GeneratedValue
    private Long id;

    @NotBlank
    private String title;

    @NotBlank
    private String content;

    private LocalDateTime postedAt;

    private boolean pinned; // 상단 고정

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    @Builder
    private Post(String title, String content, boolean pinned, LocalDateTime postedAt, Board board) {
        this.title = title;
        this.content = content;
        this.pinned = pinned;
        this.postedAt = postedAt;
        this.board = board;
    }

    public static Post of(PostCreateReqDTO req, Board board) {
        return Post.builder()
                .title(req.title())
                .content(req.content())
                .pinned(req.pinned() != null ? req.pinned() : false)
                .postedAt(req.postedAt() != null ? req.postedAt() : LocalDateTime.now())
                .board(board)
                .build();
    }

    public void update(PostUpdateReqDTO req) {
        this.title = req.title();
        this.content = req.content();
        this.pinned = req.pinned() != null ? req.pinned() : this.pinned;
        this.postedAt = req.postedAt() != null ? req.postedAt() : this.postedAt;
    }
}