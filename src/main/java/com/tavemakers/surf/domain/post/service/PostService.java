package com.tavemakers.surf.domain.post.service;

import com.tavemakers.surf.domain.board.entity.Board;
import com.tavemakers.surf.domain.board.exception.BoardNotFoundException;
import com.tavemakers.surf.domain.board.repository.BoardRepository;
import com.tavemakers.surf.domain.member.entity.Member;
import com.tavemakers.surf.domain.member.exception.MemberNotFoundException;
import com.tavemakers.surf.domain.member.repository.MemberRepository;
import com.tavemakers.surf.domain.post.dto.req.PostCreateReqDTO;
import com.tavemakers.surf.domain.post.dto.req.PostUpdateReqDTO;
import com.tavemakers.surf.domain.post.dto.res.PostResDTO;
import com.tavemakers.surf.domain.post.entity.Post;
import com.tavemakers.surf.domain.post.exception.PostNotFoundException;
import com.tavemakers.surf.domain.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class PostService {

    private final BoardRepository boardRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public PostResDTO createPost(PostCreateReqDTO req, Long memberId) {
        Board board = boardRepository.findById(req.boardId())
                .orElseThrow(BoardNotFoundException::new);

        Member member = memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);

        Post post = Post.of(req, board, member);
        Post saved = postRepository.save(post);

        return PostResDTO.from(saved);
    }

    @Transactional(readOnly = true)
    public PostResDTO getPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(PostNotFoundException::new);
        return PostResDTO.from(post);
    }

    @Transactional(readOnly = true)
    public Page<PostResDTO> getPostsByMember(Long memberId, Pageable pageable) {
        if (!memberRepository.existsById(memberId)) {
            throw new MemberNotFoundException();
        }
        Page<Post> page = postRepository.findByMemberId(memberId, pageable);
        return page.map(PostResDTO::from);
    }

    @Transactional(readOnly = true)
    public Page<PostResDTO> getPostsByBoard(Long boardId, Pageable pageable) {
        if (!boardRepository.existsById(boardId)) {
            throw new BoardNotFoundException();
        }
        Page<Post> page = postRepository.findByBoardId(boardId, pageable);
        return page.map(PostResDTO::from);
    }

    @Transactional
    public PostResDTO updatePost(Long postId, PostUpdateReqDTO req) {
        Post post = postRepository.findById(postId)
                .orElseThrow(PostNotFoundException::new);

        post.update(req, post.getBoard());
        return PostResDTO.from(post);
    }

    @Transactional
    public void deletePost(Long postId) {
        if (!postRepository.existsById(postId)) {
            throw new PostNotFoundException();
        }
        postRepository.deleteById(postId);
    }
}
