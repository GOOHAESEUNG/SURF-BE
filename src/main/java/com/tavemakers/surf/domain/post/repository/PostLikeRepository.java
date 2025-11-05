package com.tavemakers.surf.domain.post.repository;

import com.tavemakers.surf.domain.member.entity.Member;
import com.tavemakers.surf.domain.post.entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    boolean existsByPostIdAndMemberId(Long postId, Long memberId);


    @Query("select pl.post.id " +
            "from PostLike pl " +
            "where pl.member.id = :memberId " +
            "and pl.post.id in :postIds")
    List<Long> findLikedPostIdsByMemberAndPostIds(
            @Param("memberId") Long memberId,
            @Param("postIds") Collection<Long> postIds
    );

    long deleteByPostIdAndMemberId(Long postId, Long memberId);

    @Query("SELECT pl.member " +
            "FROM PostLike pl " +
            "WHERE pl.post.id = :postId")
    List<Member> findLikedMembersByPostId(@Param("postId") Long postId);
}