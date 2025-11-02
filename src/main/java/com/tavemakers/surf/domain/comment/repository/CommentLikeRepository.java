package com.tavemakers.surf.domain.comment.repository;

import com.tavemakers.surf.domain.comment.entity.Comment;
import com.tavemakers.surf.domain.comment.entity.CommentLike;
import com.tavemakers.surf.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {

    /** 특정 댓글의 좋아요 단건 조회 */
    Optional<CommentLike> findByCommentAndMember(Comment comment, Member member);

    /** 특정 댓글에 특정 회원이 좋아요를 눌렀는지 여부 */
    boolean existsByCommentAndMember(Comment comment, Member member);

    /** 특정 댓글에 달린 전체 좋아요 개수 */
    long countByComment(Comment comment);
}
