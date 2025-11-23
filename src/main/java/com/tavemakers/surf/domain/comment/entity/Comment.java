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
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Where;

import java.util.ArrayList;
import java.util.List;

@Entity
@DynamicUpdate
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long rootId;

    private int depth = 0;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @OneToMany(mappedBy = "comment", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<CommentLike> likes = new ArrayList<>();

    @Column(nullable = false, length = 1000)
    private String content;

    @Column(nullable = false)
    private boolean deleted = false;

    @Column(nullable = false)
    private Long likeCount = 0L;

    @Builder
    private Comment(Post post, Member member, String content, Long rootId, int depth) {
        this.post = post;
        this.member = member;
        this.content = content;
        this.rootId = rootId;
        this.depth = depth;
    }

    /** 루트 댓글 생성 */
    public static Comment root(Post post, Member member, String content) {
        return Comment.builder()
                .post(post)
                .member(member)
                .content(content)
                .depth(0)
                .build();
    }

    /** 대댓글 생성 */
    public static Comment child(Post post, Member member, String content, Comment root) {
        if (root.depth != 0) throw new CommentDepthExceedException();
        return Comment.builder()
                .post(post)
                .member(member)
                .content(content)
                .rootId(root.id)
                .depth(1)
                .build();
    }

    /** 저장 후 rootId 세팅 (루트 댓글일 때만) */
    public void markAsRoot() {
        if (this.depth == 0 && this.rootId == null) this.rootId = this.id;
    }

    /** 좋아요 증가 */
    public void increaseLikeCount() {
        this.likeCount++;
    }

    /** 좋아요 감소 */
    public void decreaseLikeCount() {
        if (this.likeCount > 0) this.likeCount--;
    }
}

