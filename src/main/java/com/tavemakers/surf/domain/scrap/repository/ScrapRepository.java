package com.tavemakers.surf.domain.scrap.repository;

import com.tavemakers.surf.domain.post.entity.Post;
import com.tavemakers.surf.domain.scrap.entity.Scrap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ScrapRepository extends JpaRepository<Scrap, Long> {

    boolean existsByMemberIdAndPostId(Long memberId, Long postId);

    int deleteByMemberIdAndPostId(Long memberId, Long postId);

    @Query(
            value = """
            select s.post
            from Scrap s
            where s.member.id = :memberId
            """,
            countQuery = """
            select count(s)
            from Scrap s
            where s.member.id = :memberId
            """
    )
    Page<Post> findPostsByMemberId(Long memberId, Pageable pageable);
}