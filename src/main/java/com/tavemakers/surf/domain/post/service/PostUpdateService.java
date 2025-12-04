package com.tavemakers.surf.domain.post.service;

import com.tavemakers.surf.domain.post.dto.PostViewUpdateDto;
import com.tavemakers.surf.domain.post.repository.PostJdbcRepositoryImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostUpdateService {

    private final PostJdbcRepositoryImpl postRepository;

    public void updateViewCount(List<PostViewUpdateDto> updateDtoList) {
        postRepository.viewCountBulkUpdate(updateDtoList);
    }

}
