package com.tavemakers.surf.domain.post.service.image;

import com.tavemakers.surf.domain.post.entity.PostImageUrl;
import com.tavemakers.surf.domain.post.repository.PostImageUrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostImageGetService {

    private final PostImageUrlRepository repository;

    /** 게시글 이미지 URL 목록 조회 */
    public List<PostImageUrl> getPostImageUrls(Long postId) {
        return repository.findByPostId(postId);
    }

}
