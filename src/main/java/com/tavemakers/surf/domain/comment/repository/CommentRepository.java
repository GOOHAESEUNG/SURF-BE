package com.tavemakers.surf.domain.comment.repository;

import com.tavemakers.surf.domain.comment.entity.Comment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    /** 자식 댓글 존재 여부 확인용 (삭제 시) */
    boolean existsByParentId(Long parentId);

    /** 본인 댓글만 삭제 */
    int deleteByIdAndPostIdAndMemberId(Long id, Long postId, Long memberId);

    /** 게시글 내 모든 댓글 + 대댓글 조회 (작성 시간순) */
    Slice<Comment> findByPostIdOrderByCreatedAtAsc(Long postId, Pageable pageable);
}