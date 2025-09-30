package com.tavemakers.surf.domain.post.repository;

import com.tavemakers.surf.domain.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<Post, Long> {
    Slice<Post> findByBoardId(Long boardId, Pageable pageable);

    Slice<Post> findByMemberId(Long memberId, Pageable pageable);

    @Query("select p.version from Post p where p.id = :id")
    Long findVersionById(@Param("id") Long id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Post p set p.scrapCount = p.scrapCount + 1, p.version = p.version + 1 " +
            "where p.id = :id and p.version = :version")
    int increaseScrapCount(@Param("id") Long id, @Param("version") Long version);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Post p set p.scrapCount = p.scrapCount - 1, p.version = p.version + 1 " +
            "where p.id = :id and p.version = :version and p.scrapCount > 0")
    int decreaseScrapCount(@Param("id") Long id, @Param("version") Long version);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Post p set p.likeCount = p.likeCount + 1 where p.id = :postId")
    void increaseLikeCount(@Param("postId") Long postId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Post p set p.likeCount = case when p.likeCount > 0 then p.likeCount - 1 else 0 end where p.id = :postId")
    void decreaseLikeCount(@Param("postId") Long postId);
}
