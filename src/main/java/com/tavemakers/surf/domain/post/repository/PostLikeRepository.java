package com.tavemakers.surf.domain.post.repository;

import com.tavemakers.surf.domain.post.entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.Set;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    boolean existsByPostIdAndMemberId(Long postId, Long memberId);


    @Query("""
           select pl.post.id
           from PostLike pl
           where pl.member.id = :memberId
             and pl.post.id in :postIds
           """)
    Set<Long> findLikedPostIdsByMemberAndPostIds(Long memberId, Collection<Long> postIds);

    long deleteByPostIdAndMemberId(Long postId, Long memberId);
}