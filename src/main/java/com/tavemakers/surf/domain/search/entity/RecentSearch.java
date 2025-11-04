package com.tavemakers.surf.domain.search.entity;

import com.tavemakers.surf.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "recent_search",
        uniqueConstraints = @UniqueConstraint(name="uk_member_keyword", columnNames = {"member_id","keyword"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecentSearch extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false, length = 100)
    private String keyword;

    @Column(nullable = false)
    private LocalDateTime searchedAt;

    private RecentSearch(Member member, String keyword) {
        this.member = member;
        this.keyword = keyword;
        this.searchedAt = LocalDateTime.now();
    }
    public static RecentSearch of(Member member, String keyword){ return new RecentSearch(member, keyword); }
    public void touch() { this.searchedAt = LocalDateTime.now(); }
}