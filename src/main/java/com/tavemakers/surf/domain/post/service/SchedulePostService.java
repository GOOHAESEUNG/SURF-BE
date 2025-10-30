package com.tavemakers.surf.domain.post.service;

import com.tavemakers.surf.domain.post.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SchedulePostService {
    private final ScheduleRepository scheduleRepository;

    @Transactional
    public void createSchedule(){

    }
}
