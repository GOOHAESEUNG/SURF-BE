package com.tavemakers.surf.domain.comment.repository;

import com.tavemakers.surf.domain.comment.entity.Comment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    boolean existsByRootIdAndDepth(Long rootId, int depth);

    /** 본인 댓글만 삭제 */
    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(value = "DELETE FROM comment WHERE id = :id AND post_id = :postId AND member_id = :memberId", nativeQuery = true)
    int deleteByIdAndPostIdAndMemberId(Long id, Long postId, Long memberId);

    /** 게시글 내 모든 댓글 + 대댓글 조회 (작성 시간순) */
    Slice<Comment> findByPostIdOrderByCreatedAtAsc(Long postId, Pageable pageable);

    /** 댓글 총 개수 */
    long countByPostIdAndDeletedFalse(Long postId);

    /** 대댓글이 있는 루트댓글의 경우 소프트 삭제 */
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Comment c SET c.deleted = true, c.content = '(삭제된 댓글입니다.)' WHERE c.id = :id")
    void softDeleteById(Long id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from Comment c where c.post.id = :postId")
    void deleteAllByPostId(@Param("postId") Long postId);
}


