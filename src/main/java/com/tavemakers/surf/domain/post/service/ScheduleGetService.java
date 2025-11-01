package com.tavemakers.surf.domain.post.service;

import com.tavemakers.surf.domain.post.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScheduleGetService {

    private final ScheduleRepository scheduleRepository;
}
