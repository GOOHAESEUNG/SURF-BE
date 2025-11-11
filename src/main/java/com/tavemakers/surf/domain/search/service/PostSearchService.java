package com.tavemakers.surf.domain.search.service;

import com.tavemakers.surf.domain.post.dto.res.PostResDTO;
import com.tavemakers.surf.domain.post.entity.Post;
import com.tavemakers.surf.domain.post.repository.PostLikeRepository;
import com.tavemakers.surf.domain.post.repository.PostRepository;
import com.tavemakers.surf.domain.scrap.repository.ScrapRepository;
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
public class PostSearchService {

    private final PostRepository postRepository;
    private final ScrapRepository scrapRepository;
    private final PostLikeRepository postLikeRepository;
    private final RecentSearchService recentSearchService;

    private record Flags(Set<Long> scrappedIds, Set<Long> likedIds) {}

    public Slice<PostResDTO> search(Long viewerId, String q, Pageable pageable) {
        // 1) 게시글 검색
        Slice<Post> slice = postRepository
                .findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(q, q, pageable);

        // 2) 최근 검색어 저장
        recentSearchService.saveQuery(viewerId, q);

        // 3) viewer 기준 scrapped / liked 플래그 조회
        Flags flags = resolveFlags(viewerId, slice);

        // 4) Post → PostResDTO 매핑 (이미 쓰던 패턴 그대로)
        return slice.map(p -> toRes(p, flags.scrappedIds, flags.likedIds));
    }

    private Flags resolveFlags(Long viewerId, Slice<Post> slice) {
        List<Long> ids = slice.getContent().stream()
                .map(Post::getId)
                .toList();

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