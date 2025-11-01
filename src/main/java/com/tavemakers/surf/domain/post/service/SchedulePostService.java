package com.tavemakers.surf.domain.post.service;

import com.tavemakers.surf.domain.post.dto.req.ScheduleCreateReqDto;
import com.tavemakers.surf.domain.post.entity.Post;
import com.tavemakers.surf.domain.post.entity.Schedule;
import com.tavemakers.surf.domain.post.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SchedulePostService {
    private final ScheduleRepository scheduleRepository;

    @Transactional
    public void createSchedule(ScheduleCreateReqDto dto, Post post) {
        Schedule schedule = Schedule.of(dto, post);
        scheduleRepository.save(schedule);
    }
}
