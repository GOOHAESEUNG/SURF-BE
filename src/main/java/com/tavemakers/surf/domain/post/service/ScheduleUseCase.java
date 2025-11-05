package com.tavemakers.surf.domain.post.service;

import com.tavemakers.surf.domain.post.dto.req.ScheduleCreateReqDTO;
import com.tavemakers.surf.domain.post.entity.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScheduleUseCase {

    private final ScheduleCreateService scheduleCreateService;
    private final PostService postService;

    public void createScheduleAtPost(ScheduleCreateReqDTO dto, Long postId) {
        Post post = postService.findPostById(postId);
        scheduleCreateService.createScheduleAtPost(dto, post);
    }

    public void createScheduleSingle(ScheduleCreateReqDTO dto){
        scheduleCreateService.createScheduleSingle(dto);
    }
}
