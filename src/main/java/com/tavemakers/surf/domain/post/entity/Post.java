package com.tavemakers.surf.domain.post.entity;

import com.tavemakers.surf.domain.board.entity.Board;
import com.tavemakers.surf.domain.member.entity.Member;
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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String title;

    @NotBlank
    private String content;

    private LocalDateTime postedAt;

    private boolean pinned; // 상단 고정

    @Column(nullable = false)
    private long scrapCount = 0L;

    @Version
    private Long version;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Builder
    private Post(String title, String content, boolean pinned, long scrapCount, LocalDateTime postedAt, Board board, Member member) {
        this.title = title;
        this.content = content;
        this.pinned = pinned;
        this.scrapCount = scrapCount;
        this.postedAt = postedAt;
        this.board = board;
        this.member = member;
    }

    public static Post of(PostCreateReqDTO req, Board board, Member member) {
        return Post.builder()
                .title(req.title())
                .content(req.content())
                .pinned(req.pinned() != null ? req.pinned() : false)
                .postedAt(req.postedAt() != null ? req.postedAt() : LocalDateTime.now())
                .board(board)
                .member(member)
                .scrapCount(0L)
                .build();
    }

    public void update(PostUpdateReqDTO req, Board board) {
        this.title = req.title();
        this.content = req.content();
        this.pinned = req.pinned() != null ? req.pinned() : this.pinned;
        this.board = board;
    }
}