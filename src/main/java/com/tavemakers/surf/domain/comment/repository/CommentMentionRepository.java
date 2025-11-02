package com.tavemakers.surf.domain.comment.repository;

import com.tavemakers.surf.domain.comment.entity.Comment;
import com.tavemakers.surf.domain.comment.entity.CommentLike;
import com.tavemakers.surf.domain.comment.entity.CommentMention;
import com.tavemakers.surf.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentMentionRepository extends JpaRepository<CommentMention, Long> {

    /** 특정 댓글 ID로 연결된 모든 멘션 조회 */
    List<CommentMention> findByCommentId(Long commentId);

    /** 특정 회원이 멘션된 CommentMention 목록 조회 */
    List<CommentMention> findAllByMentionedMember(Member member);

    /** 특정 댓글 + 회원 조합 존재 여부 (exists 빠른 체크용) */
    boolean existsByCommentAndMentionedMember(Comment comment, Member member);

    /** 특정 댓글에 달린 멘션 데이터를 모두 삭제 */
    void deleteAllByComment(Comment comment);
}
