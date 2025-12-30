package com.tavemakers.surf.domain.post.service;

import com.tavemakers.surf.domain.post.entity.PostImageUrl;
import com.tavemakers.surf.domain.post.repository.PostImageUrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostImageGetService {

    private final PostImageUrlRepository repository;

    public List<PostImageUrl> getPostImageUrls(Long postId) {
        return repository.findByPostId(postId);
    }

}
