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
import com.tavemakers.surf.domain.scrap.service.ScrapService;
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

    private final ScrapService scrapService;

    @Transactional
    public PostResDTO createPost(PostCreateReqDTO req, Long memberId) {
        Board board = boardRepository.findById(req.boardId())
                .orElseThrow(BoardNotFoundException::new);

        Member member = memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);

        Post post = Post.of(req, board, member);
        Post saved = postRepository.save(post);

        return PostResDTO.from(saved, false);
    }

    @Transactional(readOnly = true)
    public PostResDTO getPost(Long postId, Long memberId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(PostNotFoundException::new);

        boolean scrappedByMe = scrapService.isScrappedByMe(memberId, postId);

        return PostResDTO.from(post, scrappedByMe);
    }

    @Transactional(readOnly = true)
    public Page<PostResDTO> getMyPosts(Long myId, Pageable pageable) {
        if (!memberRepository.existsById(myId))
            throw new MemberNotFoundException();

        Page<Post> page = postRepository.findByMemberId(myId, pageable);
        return page.map(p -> PostResDTO.from(
                p,
                scrapService.isScrappedByMe(myId, p.getId())
        ));
    }

    @Transactional(readOnly = true)
    public Page<PostResDTO> getPostsByMember(Long authorId, Long viewerId, Pageable pageable) {
        if (!memberRepository.existsById(authorId)) throw new MemberNotFoundException();

        Page<Post> page = postRepository.findByMemberId(authorId, pageable);
        return page.map(p -> PostResDTO.from(
                p,
                scrapService.isScrappedByMe(viewerId, p.getId())
        ));
    }

    @Transactional(readOnly = true)
    public Page<PostResDTO> getPostsByBoard(Long boardId, Long viewerId, Pageable pageable) {
        if (!boardRepository.existsById(boardId)) {
            throw new BoardNotFoundException();
        }
        Page<Post> page = postRepository.findByBoardId(boardId, pageable);
        return page.map(p -> PostResDTO.from(
                p,
                scrapService.isScrappedByMe(viewerId, p.getId())
        ));
    }

    @Transactional
    public PostResDTO updatePost(Long postId, PostUpdateReqDTO req, Long viewerId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(PostNotFoundException::new);

        post.update(req, post.getBoard());
        boolean scrappedByMe = scrapService.isScrappedByMe(viewerId, postId);
        return PostResDTO.from(post, scrappedByMe);
    }

    @Transactional
    public void deletePost(Long postId) {
        if (!postRepository.existsById(postId)) {
            throw new PostNotFoundException();
        }
        postRepository.deleteById(postId);
    }
}
