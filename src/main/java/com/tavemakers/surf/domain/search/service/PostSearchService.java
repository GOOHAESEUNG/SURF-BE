package com.tavemakers.surf.domain.search.service;

import com.tavemakers.surf.domain.post.dto.res.PostResDTO;
import com.tavemakers.surf.domain.post.entity.Post;
import com.tavemakers.surf.domain.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostSearchService {
    private final PostRepository postRepository;
    private final RecentSearchService recentSearchService;

    @Transactional(readOnly = true)
    public Slice<PostResDTO> search(Long memberId, String q, Pageable pageable) {
        Slice<PostResDTO> result = postRepository
                .findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(q, q, pageable);
        recentSearchService.saveQuery(memberId, q); // 검색 성공/실패와 무관하게 기록
        return result;
    }
}
