package com.tavemakers.surf.domain.post.repository;

import com.tavemakers.surf.domain.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}
