package com.tavemakers.surf.domain.post.service;

import com.tavemakers.surf.domain.post.entity.PostImageUrl;
import com.tavemakers.surf.domain.post.repository.PostImageUrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostImageDeleteService {

    private final PostImageUrlRepository repository;

    public void deleteAll(List<PostImageUrl> before) {
        repository.deleteAllInBatch(before);
    }

}
