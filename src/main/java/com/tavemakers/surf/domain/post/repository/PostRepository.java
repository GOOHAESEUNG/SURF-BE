package com.tavemakers.surf.domain.post.repository;

import com.tavemakers.surf.domain.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findByBoardId(Long boardId, Pageable pageable);

    Page<Post> findByMemberId(Long memberId, Pageable pageable);
    Page<Post> findByMemberIdAndBoardId(Long memberId, Long boardId, Pageable pageable);
}
