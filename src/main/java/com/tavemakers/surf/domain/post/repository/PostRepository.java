package com.tavemakers.surf.domain.post.repository;

import com.tavemakers.surf.domain.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("select p from Post p join p.member m " +
            "where p.id = :postId and m.isDeleted = false")
    Optional<Post> findByIdAndMemberActive(@Param("postId") Long postId);

    @Query("select p from Post p join p.member m " +
            "where p.board.id = :boardId and m.isDeleted = false")
    Page<Post> findByBoardId(@Param("boardId") Long boardId, Pageable pageable);

    // 특정 회원 글 목록 → 탈퇴하지 않은 회원 글만
    @Query("select p from Post p join p.member m " +
            "where m.id = :memberId and m.isDeleted = false")
    Page<Post> findByMemberId(@Param("memberId") Long memberId, Pageable pageable);

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
}
