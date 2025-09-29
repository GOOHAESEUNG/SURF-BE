package com.tavemakers.surf.domain.comment.service;

import com.tavemakers.surf.domain.comment.dto.req.CommentCreateReqDTO;
import com.tavemakers.surf.domain.comment.dto.req.CommentUpdateReqDTO;
import com.tavemakers.surf.domain.comment.dto.res.CommentResDTO;
import com.tavemakers.surf.domain.comment.entity.Comment;
import com.tavemakers.surf.domain.comment.exception.CommentDepthExceedException;
import com.tavemakers.surf.domain.comment.exception.CommentNotFoundException;
import com.tavemakers.surf.domain.comment.exception.NotMyCommentException;
import com.tavemakers.surf.domain.comment.repository.CommentRepository;
import com.tavemakers.surf.domain.member.entity.Member;
import com.tavemakers.surf.domain.member.exception.MemberNotFoundException;
import com.tavemakers.surf.domain.member.repository.MemberRepository;
import com.tavemakers.surf.domain.post.entity.Post;
import com.tavemakers.surf.domain.post.exception.PostNotFoundException;
import com.tavemakers.surf.domain.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public CommentResDTO createComment(Long postId, Long memberId, CommentCreateReqDTO req) {
        Post post = postRepository.findById(postId).orElseThrow(PostNotFoundException::new);
        Member member = memberRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);

        Comment saved;
        if (req.parentId() == null) {
            // 루트 댓글
            Comment comment = Comment.root(post, member, req.content());
            saved = commentRepository.save(comment);
            saved.markAsRoot();          // 저장 후 rootId = id
        } else {
            // 대댓글 (한 단계만 허용)
            Comment parent = commentRepository.findById(req.parentId())
                    .orElseThrow(CommentNotFoundException::new);
            if (!parent.getPost().getId().equals(postId)) {
                throw new CommentNotFoundException();
            }
            if (parent.getDepth() >= 1) {   // 루트(0) 아래 한 단계(1)까지만 허용
                throw new CommentDepthExceedException();
            }
            Comment comment = Comment.child(post, member, req.content(), parent);
            saved = commentRepository.save(comment);
        }

        post.increaseCommentCount();
        return CommentResDTO.from(saved);
    }

    @Transactional
    public CommentResDTO updateComment(Long postId, Long commentId, Long memberId, CommentUpdateReqDTO req) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(CommentNotFoundException::new);
        if (!comment.getPost().getId().equals(postId) || !comment.getMember().getId().equals(memberId)) {
            throw new NotMyCommentException();
        }
        comment.update(req.content());

        return CommentResDTO.from(comment);
    }

    @Transactional
    public void deleteComment(Long postId, Long commentId, Long memberId) {
        long deleted = commentRepository.deleteByIdAndPostIdAndMemberId(commentId, postId, memberId);
        if (deleted > 0) {
            Post post = postRepository.findById(postId).orElseThrow(PostNotFoundException::new);
            post.decreaseCommentCount();
        }
    }

    @Transactional(readOnly = true)
    public Slice<CommentResDTO> getComments(Long postId, Pageable pageable) {
        return commentRepository.findByPostId(postId, pageable)
                .map(CommentResDTO::from);
    }
}
