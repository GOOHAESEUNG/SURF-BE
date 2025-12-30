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

    //게시글과 매핑된 스케쥴 아이디 추가

}
