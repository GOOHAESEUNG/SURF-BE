package com.tavemakers.surf.domain.post.entity;

import com.tavemakers.surf.domain.board.entity.Board;
import com.tavemakers.surf.domain.board.entity.BoardCategory;
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

    private long likeCount = 0L;

    private long commentCount = 0L;

    @Version
    private Long version;

    private LocalDateTime reservation;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;
    private String boardName;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private BoardCategory category;
    private String categoryName;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Builder
    private Post(String title, String content, boolean pinned, long scrapCount, long likeCount, long commentCount, LocalDateTime postedAt, Board board, BoardCategory category, Member member) {
        this.title = title;
        this.content = content;
        this.pinned = pinned;
        this.scrapCount = scrapCount;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.postedAt = postedAt;
        this.board = board;
        this.boardName = board.getName();
        this.category = category;
        this.categoryName = category.getName();
        this.member = member;
    }

    public static Post of(PostCreateReqDTO req, Board board, BoardCategory category, Member member) {
        return Post.builder()
                .title(req.title())
                .content(req.content())
                .pinned(req.pinned() != null ? req.pinned() : false)
                .postedAt(LocalDateTime.now())
                .board(board)
                .category(category)
                .member(member)
                .scrapCount(0L)
                .likeCount(0L)
                .commentCount(0L)
                .build();
    }

    public void update(PostUpdateReqDTO req, Board board, BoardCategory category) {
        this.title = req.title();
        this.content = req.content();
        this.pinned = req.pinned() != null ? req.pinned() : this.pinned;
        this.board = board;
        this.boardName = board.getName();
        this.category = category;
        this.categoryName = category.getName();
    }

    public void increaseCommentCount() { this.commentCount++; }
    public void decreaseCommentCount() { if (this.commentCount > 0) this.commentCount--; }

    @PrePersist
    void syncNamesOnInsert() {
        // INSERT 전에 한 번 더 동기화 (NPE 방지)
        if (board != null) this.boardName = board.getName();
        if (category != null) this.categoryName = category.getName();
    }

    @PreUpdate
    void syncNamesOnUpdate() {
        // UPDATE 직전에 항상 최신 이름으로 갱신
        if (board != null) this.boardName = board.getName();
        if (category != null) this.categoryName = category.getName();
    }
}