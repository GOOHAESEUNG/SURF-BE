package com.tavemakers.surf.domain.comment.service;

import com.tavemakers.surf.domain.comment.repository.CommentLikeRepository;
import com.tavemakers.surf.domain.comment.repository.CommentMentionRepository;
import com.tavemakers.surf.domain.comment.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 댓글 삭제 전용 서비스 */
@Service
@RequiredArgsConstructor
public class CommentDeleteService {

    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final CommentMentionRepository commentMentionRepository;

    /** 게시글의 모든 댓글 삭제 (연관 데이터 먼저 삭제) */
    @Transactional
    public void deleteAllByPostId(Long postId) {
        commentLikeRepository.deleteAllByPostId(postId);
        commentMentionRepository.deleteAllByPostId(postId);
        commentRepository.deleteRepliesByPostId(postId);
        commentRepository.deleteRootCommentsByPostId(postId);
    }
}
