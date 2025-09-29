package com.tavemakers.surf.domain.comment.repository;

import com.tavemakers.surf.domain.comment.entity.Comment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    long deleteByIdAndPostIdAndMemberId(Long id, Long postId, Long memberId);

    Slice<Comment> findByPostId(Long postId, Pageable pageable);
}