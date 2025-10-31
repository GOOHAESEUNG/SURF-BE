package com.tavemakers.surf.domain.post.service;

import com.tavemakers.surf.domain.post.dto.req.PostImageCreateReqDTO;
import com.tavemakers.surf.domain.post.dto.res.PostImageResDTO;
import com.tavemakers.surf.domain.post.entity.Post;
import com.tavemakers.surf.domain.post.entity.PostImageUrl;
import com.tavemakers.surf.domain.post.repository.PostImageUrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostImageSaveService {

    private final PostImageUrlRepository repository;

    @Transactional
    public List<PostImageResDTO> saveAll(Post post, List<PostImageCreateReqDTO> dto) {
        List<PostImageUrl> imageUrlList = dto.stream()
                .map(url -> PostImageUrl.of(post, url))
                .toList();
        return repository.saveAll(imageUrlList)
                .stream()
                .map(PostImageResDTO::from)
                .sorted(Comparator.comparing(PostImageResDTO::sequence))
                .toList();
    }

}
