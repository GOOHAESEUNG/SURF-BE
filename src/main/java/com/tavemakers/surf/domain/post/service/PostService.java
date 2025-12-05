package com.tavemakers.surf.domain.post.service;

import com.tavemakers.surf.domain.board.entity.Board;
import com.tavemakers.surf.domain.board.entity.BoardCategory;
import com.tavemakers.surf.domain.board.exception.BoardNotFoundException;
import com.tavemakers.surf.domain.board.exception.CategoryNotFoundException;
import com.tavemakers.surf.domain.board.exception.CategoryRequiredException;
import com.tavemakers.surf.domain.board.exception.InvalidCategoryMappingException;
import com.tavemakers.surf.domain.board.repository.BoardCategoryRepository;
import com.tavemakers.surf.domain.board.repository.BoardRepository;
import com.tavemakers.surf.domain.comment.repository.CommentRepository;
import com.tavemakers.surf.domain.member.entity.Member;
import com.tavemakers.surf.domain.member.exception.MemberNotFoundException;
import com.tavemakers.surf.domain.member.repository.MemberRepository;
import com.tavemakers.surf.domain.member.service.MemberGetService;
import com.tavemakers.surf.domain.post.dto.req.PostCreateReqDTO;
import com.tavemakers.surf.domain.post.dto.req.PostImageCreateReqDTO;
import com.tavemakers.surf.domain.post.dto.req.PostUpdateReqDTO;
import com.tavemakers.surf.domain.post.dto.res.PostDetailResDTO;
import com.tavemakers.surf.domain.post.dto.res.PostImageResDTO;
import com.tavemakers.surf.domain.post.dto.res.PostResDTO;
import com.tavemakers.surf.domain.post.entity.Post;
import com.tavemakers.surf.domain.post.entity.PostImageUrl;
import com.tavemakers.surf.domain.post.exception.PostDeleteAccessDeniedException;
import com.tavemakers.surf.domain.post.exception.PostImageListEmptyException;
import com.tavemakers.surf.domain.post.exception.PostNotFoundException;
import com.tavemakers.surf.domain.post.repository.PostLikeRepository;
import com.tavemakers.surf.domain.post.repository.PostRepository;
import com.tavemakers.surf.domain.post.repository.ScheduleRepository;
import com.tavemakers.surf.domain.reservation.usecase.ReservationUsecase;
import com.tavemakers.surf.domain.scrap.repository.ScrapRepository;
import com.tavemakers.surf.domain.scrap.service.ScrapService;
import com.tavemakers.surf.global.logging.LogEvent;
import com.tavemakers.surf.global.logging.LogParam;
import com.tavemakers.surf.global.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import java.util.Comparator;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final BoardRepository boardRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final BoardCategoryRepository boardCategoryRepository;
    private final ScheduleRepository scheduleRepository;
    private final ScrapRepository scrapRepository;
    private final CommentRepository commentRepository;
    private final PostLikeRepository postLikeRepository;

    private final ScrapService scrapService;
    private final PostLikeService postLikeService;
    private final ReservationUsecase reservationUsecase;
    private final PostImageSaveService imageSaveService;
    private final PostImageGetService imageGetService;
    private final PostImageDeleteService imageDeleteService;
    private final PostGetService postGetService;
    private final PostImageGetService postImageGetService;
    private final PostImageDeleteService postImageDeleteService;
    private final MemberGetService memberGetService;
    private final ViewCountService viewCountService;
    private final FlagsMapper flagsMapper;


    @Transactional
    @LogEvent(value = "post.create", message = "게시글 생성 성공")
    public PostDetailResDTO createPost(PostCreateReqDTO req, Long memberId) {
        Board board = boardRepository.findById(req.boardId())
                .orElseThrow(BoardNotFoundException::new);
        Member member = memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);

        BoardCategory category = resolveCategory(board, req.categoryId());

        Post post = Post.of(req, board, category, member);
        Post saved = postRepository.save(post);

        if (req.isReserved()) {
            reservationUsecase.reservePost(saved.getId(), req.reservedAt());
        }

        if (req.hasImage()) {
            List<PostImageCreateReqDTO> imageUrlList = req.imageUrlList();
            saved.addThumbnailUrl(findFirstImage(imageUrlList));
            List<PostImageResDTO> imageUrlResponseList = imageSaveService.saveAll(saved, imageUrlList);
            return PostDetailResDTO.of(saved, false, false, true, imageUrlResponseList, 0);
        }

        return PostDetailResDTO.of(saved, false, false,true,null, 0);
    }

    @Transactional
    public PostDetailResDTO getPost(Long postId, Long memberId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(PostNotFoundException::new);
        boolean scrappedByMe = scrapService.isScrappedByMe(memberId, postId);
        boolean likedByMe = postLikeService.isLikedByMe(memberId, postId);
        boolean isMine = post.isOwner(memberId);
        List<PostImageResDTO> imageUrlList = getImageUrlList(post);
        int viewCount = viewCountService.increaseViewCount(post, memberId);
        return PostDetailResDTO.of(post, scrappedByMe, likedByMe, isMine, imageUrlList, viewCount);
    }

    @Transactional(readOnly = true)
    public Slice<PostResDTO> getMyPosts(Long myId, Pageable pageable) {
        if (!memberRepository.existsById(myId))
            throw new MemberNotFoundException();
        Slice<Post> slice = postRepository.findByMemberId(myId, pageable);
        FlagsMapper.Flags flags = flagsMapper.resolveFlags(myId, slice.getContent());
        return slice.map(p -> flagsMapper.toRes(p, flags));
    }

    @Transactional(readOnly = true)
    public Slice<PostResDTO> getPostsByMember(Long authorId, Long viewerId, Pageable pageable) {
        if (!memberRepository.existsById(authorId))
            throw new MemberNotFoundException();
        Slice<Post> slice = postRepository.findByMemberId(authorId, pageable);
        FlagsMapper.Flags flags = flagsMapper.resolveFlags(viewerId, slice.getContent());
        return slice.map(p -> flagsMapper.toRes(p, flags));
    }

    @Transactional(readOnly = true)
    public Slice<PostResDTO> getPostsByBoardAndCategory(
            Long boardId, String categorySlug, Long viewerId, Pageable pageable) {

        Board board = boardRepository.findById(boardId)
                .orElseThrow(BoardNotFoundException::new);

        Member viewer = memberGetService.getMember(viewerId);
        boolean isManager = viewer.hasDeleteRole();

        final boolean all = (categorySlug == null || categorySlug.isBlank() || "all".equalsIgnoreCase(categorySlug));

        Slice<Post> slice;
        if (all) {
            slice = isManager
                    ? postRepository.findByBoardId(boardId, pageable)
                    : postRepository.findByBoardIdAndIsReservedFalse(boardId, pageable);
        } else {
            // 보드-카테고리 소속 검증 (slug 기준)
            BoardCategory category = boardCategoryRepository.findByBoardIdAndSlug(boardId, categorySlug)
                    .orElseThrow(CategoryNotFoundException::new);

            resolveCategory(board, category.getId());
            slice = isManager
                    ? postRepository.findByBoardIdAndCategoryId(boardId, category.getId(), pageable)
                    : postRepository.findByBoardIdAndCategoryIdAndIsReservedFalse(boardId, category.getId(), pageable);
        }

        FlagsMapper.Flags flags = flagsMapper.resolveFlags(viewerId, slice.getContent());
        return slice.map(p -> flagsMapper.toRes(p, flags));
    }

    @Transactional(readOnly = true)
    public Slice<PostResDTO> getPostsByBoardAndCategory(Long boardId, Long categoryId, Long viewerId, Pageable pageable) {
        Board board = boardRepository.findById(boardId).orElseThrow(BoardNotFoundException::new);
        resolveCategory(board, categoryId);
        Slice<Post> slice = postRepository.findByBoardIdAndCategoryId(boardId, categoryId, pageable);
        FlagsMapper.Flags flags = flagsMapper.resolveFlags(viewerId, slice.getContent());
        return slice.map(p -> flagsMapper.toRes(p, flags));
    }

    @Transactional
    @LogEvent(value = "post.update", message = "게시글 수정 성공")
    public PostDetailResDTO updatePost(
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
        int viewCount = viewCountService.increaseViewCount(post, viewerId);

        // 예약 시간 변경 시 -> 기존의 예약 시간 조회 -> 기존의 예약 시간을 CANCELD로 수정하고 schedule 호출하면 끝.
        if (req.isReservationChanged()) {
            reservationUsecase.updateReservationPost(post.getId(), req.reservedAt());
        }

        // 이미지 변경
        if (req.isImageChanged()) {
            deleteExistingImage(post);
            List<PostImageCreateReqDTO> changeImage = req.imageUrlList();
            post.addThumbnailUrl(findFirstImage(changeImage));
            List<PostImageResDTO> savedChangedImage = imageSaveService.saveAll(post, changeImage);
            return PostDetailResDTO.of(post, scrappedByMe, likedByMe, true, savedChangedImage, viewCount);
        }

        List<PostImageResDTO> imageDtoList = getImageUrlList(post);
        return PostDetailResDTO.of(post, scrappedByMe, likedByMe, true, imageDtoList, viewCount);
    }

    private void deleteExistingImage(Post post) {
        List<PostImageUrl> beforeImage = imageGetService.getPostImageUrls(post.getId());
        imageDeleteService.deleteAll(beforeImage);
    }

    @Transactional
    @LogEvent(value = "post.delete", message = "게시글 삭제 성공")
    public void deletePost(
            @LogParam("post_id") Long postId) {
        Post post = postGetService.getPost(postId);
        Member member = memberGetService.getMember(SecurityUtils.getCurrentMemberId());
        validateOwnerOrManager(post, member);

        scheduleRepository.deleteByPostId(postId);
        postLikeRepository.deleteByPostId(postId);
        scrapRepository.deleteByPostId(postId);
        commentRepository.deleteAllByPostId(postId);

        List<PostImageUrl> postImageUrls = postImageGetService.getPostImageUrls(post.getId());
        if (postImageUrls != null && !postImageUrls.isEmpty()) {
            postImageDeleteService.deleteAll(postImageUrls);
        }

        postRepository.delete(post);
    }

    private BoardCategory resolveCategory(Board board, Long categoryId) {
        if (categoryId == null) {
            throw new CategoryRequiredException();
        }
        BoardCategory category = boardCategoryRepository.findById(categoryId)
                .orElseThrow(CategoryNotFoundException::new);
        if (!category.getBoard().getId().equals(board.getId())) {
            throw new InvalidCategoryMappingException();
        }
        return category;
    }

    @Transactional(readOnly = true)
    public Post findPostById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow((PostNotFoundException::new));
    }

    private List<PostImageResDTO> getImageUrlList(Post post) {
        return imageGetService.getPostImageUrls(post.getId()).stream()
                .map(PostImageResDTO::from)
                .sorted(Comparator.comparing(PostImageResDTO::sequence))
                .toList();
    }

    private String findFirstImage(List<PostImageCreateReqDTO> dto) {
        if (dto == null || dto.isEmpty()) {
            throw new PostImageListEmptyException();
        }

        PostImageCreateReqDTO postImageCreateReqDTO = dto.stream()
                .min(Comparator.comparing(PostImageCreateReqDTO::sequence))
                .orElse(dto.get(0));
        return postImageCreateReqDTO.originalUrl();
    }

    private void validateOwnerOrManager(Post post, Member member) {
        if (!member.hasDeleteRole() && !post.isOwner(member.getId())) {
            throw new PostDeleteAccessDeniedException();
        }
    }

}