package com.tavemakers.surf.domain.post.service;

import com.tavemakers.surf.domain.board.entity.Board;
import com.tavemakers.surf.domain.board.exception.BoardNotFoundException;
import com.tavemakers.surf.domain.board.repository.BoardRepository;
import com.tavemakers.surf.domain.member.entity.Member;
import com.tavemakers.surf.domain.member.exception.MemberNotFoundException;
import com.tavemakers.surf.domain.member.repository.MemberRepository;
import com.tavemakers.surf.domain.post.dto.req.PostCreateReqDTO;
import com.tavemakers.surf.domain.post.dto.req.PostImageCreateReqDTO;
import com.tavemakers.surf.domain.post.dto.req.PostUpdateReqDTO;
import com.tavemakers.surf.domain.post.dto.res.PostDetailResDTO;
import com.tavemakers.surf.domain.post.dto.res.PostImageResDTO;
import com.tavemakers.surf.domain.post.dto.res.PostResDTO;
import com.tavemakers.surf.domain.post.entity.Post;
import com.tavemakers.surf.domain.post.entity.PostImageUrl;
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

import java.util.Comparator;
import java.util.List;


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
    private final PostImageSaveService imageSaveService;
    private final PostImageGetService imageGetService;
    private final PostImageDeleteService imageDeleteService;

    @Transactional
    @LogEvent(value= "post.create", message= "게시글 생성 성공")
    public PostDetailResDTO createPost(PostCreateReqDTO req, Long memberId) {
        Board board = boardRepository.findById(req.boardId())
                .orElseThrow(BoardNotFoundException::new);
        Member member = memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);
        Post post = Post.of(req, board, member);
        Post saved = postRepository.save(post);

        if (req.isReserved()) {
            reservationUsecase.reservePost(saved.getId(), req.reservedAt());
        }

        if (req.hasImage()) {
            List<PostImageCreateReqDTO> imageUrlList = req.imageUrlList();
            saved.addThumbnailUrl(findFirstImage(imageUrlList));
            List<PostImageResDTO> imageUrlResponseList = imageSaveService.saveAll(saved, imageUrlList);
            return PostDetailResDTO.of(saved, false, false, imageUrlResponseList);
        }

        return PostDetailResDTO.of(saved, false, false, null);
    }

    @Transactional(readOnly = true)
    public PostDetailResDTO getPost(Long postId, Long memberId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(PostNotFoundException::new);
        boolean scrappedByMe = scrapService.isScrappedByMe(memberId, postId);
        boolean likedByMe = postLikeService.isLikedByMe(memberId, postId);
        List<PostImageResDTO> imageUrlList = getImageUrlList(post);
        return PostDetailResDTO.of(post, scrappedByMe, likedByMe, imageUrlList);
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
    public PostDetailResDTO updatePost(
            @LogParam("post_id") Long postId,
            PostUpdateReqDTO req, Long viewerId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(PostNotFoundException::new);
        post.update(req, post.getBoard());
        boolean scrappedByMe = scrapService.isScrappedByMe(viewerId, postId);
        boolean likedByMe = postLikeService.isLikedByMe(viewerId, postId);

        // 예약 시간 변경 시 -> 기존의 예약 시간 조회 -> 기존의 예약 시간을 CANCELD로 수정하고 schedule 호출하면 끝.

        // 이미지 변경
        if (req.isImageChanged()) {
            deleteExistingImage(post);
            List<PostImageCreateReqDTO> changeImage = req.imageUrlList();
            post.addThumbnailUrl(findFirstImage(changeImage));
            List<PostImageResDTO> savedChangedImage = imageSaveService.saveAll(post, changeImage);
            return PostDetailResDTO.of(post, scrappedByMe, likedByMe, savedChangedImage);
        }

        List<PostImageResDTO> imageDtoList = getImageUrlList(post);
        return PostDetailResDTO.of(post, scrappedByMe, likedByMe, imageDtoList);
    }

    private void deleteExistingImage(Post post) {
        List<PostImageUrl> beforeImage = imageGetService.getPostImageUrls(post.getId());
        imageDeleteService.deleteAll(beforeImage);
    }

    @Transactional
    @LogEvent(value = "post.delete", message = "게시글 삭제 성공")
    public void deletePost(
            @LogParam("post_id") Long postId) {
        if (!postRepository.existsById(postId)) throw new PostNotFoundException();
        postRepository.deleteById(postId);
    }

    /*
    * refactor
    * */

    private List<PostImageResDTO> getImageUrlList(Post post) {
        return imageGetService.getPostImageUrls(post.getId()).stream()
                .map(PostImageResDTO::from)
                .sorted(Comparator.comparing(PostImageResDTO::sequence))
                .toList();
    }

    private String findFirstImage(List<PostImageCreateReqDTO> dto) {
        PostImageCreateReqDTO postImageCreateReqDTO = dto.stream()
                .min(Comparator.comparing(PostImageCreateReqDTO::sequence))
                .orElse(dto.get(0));
        return postImageCreateReqDTO.originalUrl();
    }

}