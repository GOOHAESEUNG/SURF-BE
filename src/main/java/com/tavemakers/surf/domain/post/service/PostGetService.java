package com.tavemakers.surf.domain.post.service;

import com.tavemakers.surf.domain.post.entity.Post;
import com.tavemakers.surf.domain.post.exception.PostNotFoundException;
import com.tavemakers.surf.domain.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostGetService {

    private final PostRepository postRepository;

    @Transactional
    public Post getPost(Long id) {
        return  postRepository.findById(id)
                .orElseThrow(PostNotFoundException::new);
    }

    public Post getPostOrNull(Long id) {
        return postRepository.findById(id)
                .orElse(null);
    }

    @Transactional(readOnly = true)
    public Post readPost(Long id) {
        return  postRepository.findById(id)
                .orElseThrow(PostNotFoundException::new);
    }

    @Transactional(readOnly = true)
    public void validatePost(Long id) {
        postRepository.findById(id)
                .orElseThrow(PostNotFoundException::new);
    }

    //해당 게시글이 일정과 매핑 되어있는지 판단
    @Transactional(readOnly = true)
    public boolean existsSchedule(Post post){
        return post.isScheduleExist();
    }
}
