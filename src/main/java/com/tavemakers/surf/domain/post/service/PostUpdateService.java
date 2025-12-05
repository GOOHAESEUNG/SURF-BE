package com.tavemakers.surf.domain.post.service;

import com.tavemakers.surf.domain.post.dto.PostViewUpdateDto;
import com.tavemakers.surf.domain.post.repository.PostJdbcRepositoryImpl;
import com.tavemakers.surf.domain.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostUpdateService {

    private final PostJdbcRepositoryImpl postJdbcRepository;
    private final PostRepository postRepository;

    public void updateViewCount(List<PostViewUpdateDto> updateDtoList) {
        postJdbcRepository.viewCountBulkUpdate(updateDtoList);
    }

    public void increaseViewCount(Long postId) {
        postRepository.increaseViewCount(postId);
    }

}
