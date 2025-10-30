package com.tavemakers.surf.domain.post.service;

import com.tavemakers.surf.domain.board.entity.Board;
import com.tavemakers.surf.domain.board.entity.BoardCategory;
import com.tavemakers.surf.domain.board.exception.BoardNotFoundException;
import com.tavemakers.surf.domain.board.repository.BoardCategoryRepository;
import com.tavemakers.surf.domain.board.repository.BoardRepository;
import com.tavemakers.surf.domain.member.entity.Member;
import com.tavemakers.surf.domain.member.exception.MemberNotFoundException;
import com.tavemakers.surf.domain.member.repository.MemberRepository;
import com.tavemakers.surf.domain.post.dto.req.PostCreateReqDTO;
import com.tavemakers.surf.domain.post.dto.req.PostUpdateReqDTO;
import com.tavemakers.surf.domain.post.dto.res.PostResDTO;
import com.tavemakers.surf.domain.post.entity.Post;
import com.tavemakers.surf.domain.post.exception.PostNotFoundException;
import com.tavemakers.surf.domain.post.repository.PostLikeRepository;
import com.tavemakers.surf.domain.post.repository.PostRepository;
import com.tavemakers.surf.domain.scrap.repository.ScrapRepository;
import com.tavemakers.surf.domain.scrap.service.ScrapService;
import com.tavemakers.surf.global.logging.LogEvent;
import com.tavemakers.surf.global.logging.LogParam;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final BoardRepository boardRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final BoardCategoryRepository boardCategoryRepository;
    private final ScrapRepository scrapRepository;
    private final PostLikeRepository postLikeRepository;

    private final ScrapService scrapService;
    private final PostLikeService postLikeService;

    @Transactional
    @LogEvent(value= "post.create", message= "게시글 생성 성공")
    public PostResDTO createPost(PostCreateReqDTO req, Long memberId) {
        Board board = boardRepository.findById(req.boardId())
                .orElseThrow(BoardNotFoundException::new);
        Member member = memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);

        BoardCategory category = resolveCategory(board, req.categoryId());

        Post post = Post.of(req, board, category, member);
        Post saved = postRepository.save(post);
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
        Flags flags = resolveFlags(myId, slice);
        return slice.map(p -> toRes(p, flags.scrappedIds, flags.likedIds));
    }

    @Transactional(readOnly = true)
    public Slice<PostResDTO> getPostsByMember(Long authorId, Long viewerId, Pageable pageable) {
        if (!memberRepository.existsById(authorId)) throw new MemberNotFoundException();
        Slice<Post> slice = postRepository.findByMemberId(authorId, pageable);
        Flags flags = resolveFlags(viewerId, slice);
        return slice.map(p -> toRes(p, flags.scrappedIds, flags.likedIds));
    }

    @Transactional(readOnly = true)
    public Slice<PostResDTO> getPostsByBoard(Long boardId, Long viewerId, Pageable pageable) {
        if (!boardRepository.existsById(boardId)) throw new BoardNotFoundException();
        Slice<Post> slice = postRepository.findByBoardId(boardId, pageable);
        Flags flags = resolveFlags(viewerId, slice);
        return slice.map(p -> toRes(p, flags.scrappedIds, flags.likedIds));
    }

    @Transactional
    @LogEvent(value = "post.update", message = "게시글 수정 성공")
    public PostResDTO updatePost(
            @LogParam("post_id") Long postId,
            PostUpdateReqDTO req, Long viewerId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(PostNotFoundException::new);

        BoardCategory newCategory = (req.categoryId() != null)
                ? resolveCategory(post.getBoard(), req.categoryId())
                : post.getCategory();

        post.update(req, post.getBoard(), newCategory);

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

    private BoardCategory resolveCategory(Board board, Long categoryId) {
        if (categoryId == null) {
            throw new IllegalArgumentException("카테고리는 필수입니다.");
        }
        BoardCategory category = boardCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리입니다."));
        if (!category.getBoard().getId().equals(board.getId())) {
            throw new IllegalArgumentException("카테고리가 해당 보드에 속하지 않습니다.");
        }
        return category;
    }

    private record Flags(Set<Long> scrappedIds, Set<Long> likedIds) {}

    private Flags resolveFlags(Long viewerId, Slice<Post> slice) {
        List<Long> ids = slice.getContent().stream().map(Post::getId).toList();
        Set<Long> scrappedIds = ids.isEmpty() ? Set.of()
                : scrapRepository.findScrappedPostIdsByMemberAndPostIds(viewerId, ids);
        Set<Long> likedIds = ids.isEmpty() ? Set.of()
                : postLikeRepository.findLikedPostIdsByMemberAndPostIds(viewerId, ids);
        return new Flags(scrappedIds, likedIds);
    }

    private PostResDTO toRes(Post p, Set<Long> scrapped, Set<Long> liked) {
        boolean scr = scrapped.contains(p.getId());
        boolean lk  = liked.contains(p.getId());
        return PostResDTO.from(p, scr, lk);
    }
}