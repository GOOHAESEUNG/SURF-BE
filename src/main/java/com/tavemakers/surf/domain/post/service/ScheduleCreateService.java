package com.tavemakers.surf.domain.post.service;

import com.tavemakers.surf.domain.post.dto.req.ScheduleCreateReqDTO;
import com.tavemakers.surf.domain.post.entity.Post;
import com.tavemakers.surf.domain.post.entity.Schedule;
import com.tavemakers.surf.domain.post.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ScheduleCreateService {
    private final ScheduleRepository scheduleRepository;

    @Transactional
    public Schedule createScheduleAtPost(ScheduleCreateReqDTO dto, Post post) {
        Schedule schedule = Schedule.of(dto, post);
        return scheduleRepository.save(schedule);
    }

    @Transactional
    public Schedule createScheduleSingle(ScheduleCreateReqDTO dto) {
        Schedule schedule = Schedule.from(dto);
        return scheduleRepository.save(schedule);
    }
}
