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
import com.tavemakers.surf.domain.reservation.usecase.ReservationUsecase;
import com.tavemakers.surf.domain.scrap.service.ScrapService;
import com.tavemakers.surf.global.logging.LogEvent;
import com.tavemakers.surf.global.logging.LogParam;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final BoardRepository boardRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final ScrapService scrapService;
    private final PostLikeService postLikeService;
    private final ReservationUsecase reservationUsecase;

    @Transactional
    @LogEvent(value= "post.create", message= "게시글 생성 성공")
    public PostResDTO createPost(PostCreateReqDTO req, Long memberId) {
        Board board = boardRepository.findById(req.boardId())
                .orElseThrow(BoardNotFoundException::new);
        Member member = memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);
        Post post = Post.of(req, board, member);
        Post saved = postRepository.save(post);

        if (req.isReserved()) {
            reservationUsecase.reservePost(saved.getId(), req.reservedAt());
        }

        return PostResDTO.from(saved, false, false);
    }

    @Transactional(readOnly = true)
    public PostResDTO getPost(Long postId, Long memberId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(PostNotFoundException::new);
        boolean scrappedByMe = scrapService.isScrappedByMe(memberId, postId);
        boolean likedByMe = postLikeService.isLikedByMe(memberId, postId);
        return PostResDTO.from(post, scrappedByMe, likedByMe);
    }

    @Transactional(readOnly = true)
    public Slice<PostResDTO> getMyPosts(Long myId, Pageable pageable) {
        if (!memberRepository.existsById(myId)) throw new MemberNotFoundException();
        Slice<Post> slice = postRepository.findByMemberId(myId, pageable);
        return slice.map(p -> PostResDTO.from(
                p,
                scrapService.isScrappedByMe(myId, p.getId()),
                postLikeService.isLikedByMe(myId, p.getId())
        ));
    }

    @Transactional(readOnly = true)
    public Slice<PostResDTO> getPostsByMember(Long authorId, Long viewerId, Pageable pageable) {
        if (!memberRepository.existsById(authorId)) throw new MemberNotFoundException();
        Slice<Post> slice = postRepository.findByMemberId(authorId, pageable);
        return slice.map(p -> PostResDTO.from(
                p,
                scrapService.isScrappedByMe(viewerId, p.getId()),
                postLikeService.isLikedByMe(viewerId, p.getId())
        ));
    }

    @Transactional(readOnly = true)
    public Slice<PostResDTO> getPostsByBoard(Long boardId, Long viewerId, Pageable pageable) {
        if (!boardRepository.existsById(boardId)) throw new BoardNotFoundException();
        Slice<Post> slice = postRepository.findByBoardId(boardId, pageable);
        return slice.map(p -> PostResDTO.from(
                p,
                scrapService.isScrappedByMe(viewerId, p.getId()),
                postLikeService.isLikedByMe(viewerId, p.getId())
        ));
    }

    @Transactional
    @LogEvent(value = "post.update", message = "게시글 수정 성공")
    public PostResDTO updatePost(
            @LogParam("post_id") Long postId,
            PostUpdateReqDTO req, Long viewerId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(PostNotFoundException::new);
        post.update(req, post.getBoard());
        boolean scrappedByMe = scrapService.isScrappedByMe(viewerId, postId);
        boolean likedByMe = postLikeService.isLikedByMe(viewerId, postId);
        return PostResDTO.from(post, scrappedByMe, likedByMe);
    }

    @Transactional
    @LogEvent(value = "post.delete", message = "게시글 삭제 성공")
    public void deletePost(
            @LogParam("post_id") Long postId) {
        if (!postRepository.existsById(postId)) throw new PostNotFoundException();
        postRepository.deleteById(postId);
    }

    @Transactional(readOnly = true)
    public Post findPostById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow((PostNotFoundException::new));
    }
}