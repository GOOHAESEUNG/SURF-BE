package com.tavemakers.surf.domain.post.repository;

import com.tavemakers.surf.domain.post.entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    boolean existsByPostIdAndMemberId(Long postId, Long memberId);

    Optional<PostLike> findByPostIdAndMemberId(Long postId, Long memberId);

    long countByPostId(Long postId);
}