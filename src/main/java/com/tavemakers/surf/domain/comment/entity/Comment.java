package com.tavemakers.surf.domain.comment.entity;

import com.tavemakers.surf.domain.comment.exception.CommentDepthExceedException;
import com.tavemakers.surf.domain.member.entity.Member;
import com.tavemakers.surf.domain.post.entity.Post;
import com.tavemakers.surf.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Where(clause="deleted=false")
public class Comment extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long rootId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parent;

    private int depth = 0;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false, length = 1000)
    private String content;

    @Column(nullable = false)
    private boolean deleted = false;

    public void softDelete() {
        this.deleted = true;
        this.content = "(삭제된 댓글입니다.)";
    }

    @Builder
    private Comment(Post post, Member member, String content, Comment parent, Long rootId, int depth) {
        this.post = post;
        this.member = member;
        this.content = content;
        this.parent = parent;
        this.rootId = rootId;
        this.depth = depth;
    }

    /** 루트 댓글 생성 */
    public static Comment root(Post post, Member member, String content) {
        return Comment.builder()
                .post(post)
                .member(member)
                .content(content)
                .parent(null)
                .depth(0)
                .build();
    }

    /** 대댓글 생성 */
    public static Comment child(Post post, Member member, String content, Comment parent) {
        if (parent.getDepth() >= 1) { // 루트(0) 밑의 한 단계(1)까지만 허용
            throw new CommentDepthExceedException();
        }
        Long root = (parent.getParent() == null) ? parent.getId() : parent.getRootId(); // 안전
        return Comment.builder()
                .post(post)
                .member(member)
                .content(content)
                .parent(parent)
                .rootId(root)
                .depth(parent.getDepth() + 1)
                .build();
    }

    /** 저장 후 rootId 세팅 (루트 댓글일 때만) */
    public void markAsRoot() {
        if (this.parent == null && this.rootId == null) {
            this.rootId = this.id;
        }
    }

    public void update(String content) {
        this.content = content;
    }
}