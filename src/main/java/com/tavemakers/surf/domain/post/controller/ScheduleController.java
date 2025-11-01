package com.tavemakers.surf.domain.post.controller;

import com.tavemakers.surf.domain.post.entity.Schedule;
import com.tavemakers.surf.domain.post.service.SchedulePostService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping
@Tag(name = "일정", description = "일정 생성 관련 API")
public class ScheduleController {
    private final SchedulePostService schedulePostService;


}
