package com.tavemakers.surf.domain.post.service;

import com.tavemakers.surf.domain.post.dto.req.ScheduleCreateReqDto;
import com.tavemakers.surf.domain.post.entity.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SchduleUseCase {

    private final SchedulePostService schedulePostService;
    private final PostService postService;

    public void createScheduleAtPost(ScheduleCreateReqDto dto, Long postId) {
        Post post = postService.findPostById(postId);
        schedulePostService.createScheduleAtPost(dto, post);
    }

    public void createScheduleSingle(ScheduleCreateReqDto dto){
        schedulePostService.createScheduleSingle(dto);
    }
}
