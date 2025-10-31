package com.tavemakers.surf.domain.post.service;

import com.tavemakers.surf.domain.post.entity.PostImageUrl;
import com.tavemakers.surf.domain.post.repository.PostImageUrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostImageDeleteService {

    private final PostImageUrlRepository repository;

    @Transactional
    public void deleteAll(List<PostImageUrl> beforeImages) {
        if (beforeImages == null || beforeImages.isEmpty()) {
            return;
        }
        repository.deleteAllInBatch(beforeImages);
    }

}
