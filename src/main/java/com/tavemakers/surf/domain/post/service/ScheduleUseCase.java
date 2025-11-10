package com.tavemakers.surf.domain.post.service;

import com.tavemakers.surf.domain.post.dto.req.ScheduleCreateReqDTO;
import com.tavemakers.surf.domain.post.dto.req.ScheduleUpdateReqDTO;
import com.tavemakers.surf.domain.post.entity.Post;
import com.tavemakers.surf.domain.post.entity.Schedule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ScheduleUseCase {

    private final ScheduleCreateService scheduleCreateService;
    private final ScheduleGetService scheduleGetService;
    private final SchedulePatchService schedulePatchService;
    private final PostService postService;

    //게시글 생성 시 일정 생성
    @Transactional
    public void createScheduleAtPost(ScheduleCreateReqDTO dto, Long postId) {
        Post post = postService.findPostById(postId);
        scheduleCreateService.createScheduleAtPost(dto, post);
    }

    //개별 일정 생성(캘린더에서)
    @Transactional
    public void createScheduleSingle(ScheduleCreateReqDTO dto){
        scheduleCreateService.createScheduleSingle(dto);
    }

    //일정 수정
    @Transactional
    public void updateSchedule(ScheduleUpdateReqDTO dto, Long id) {
        Schedule schedule = scheduleGetService.getScheduleById(id);
        schedulePatchService.updateSchedule(schedule, dto);
    }
}
