package com.tavemakers.surf.domain.post.controller;

import static com.tavemakers.surf.domain.post.controller.ResponseMessage.SCHEDULE_CALENDAR_READ;
import static com.tavemakers.surf.domain.post.controller.ResponseMessage.SCHEDULE_CREATED;

import com.tavemakers.surf.domain.post.dto.req.ScheduleCreateReqDTO;
import com.tavemakers.surf.domain.post.dto.res.ScheduleMonthlyResDTO;
import com.tavemakers.surf.domain.post.service.ScheduleGetService;
import com.tavemakers.surf.global.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping
@Tag(name = "일정 조회", description = "일정 조회 관련 API")
public class ScheduleGetController {

    private final ScheduleGetService scheduleGetService;

    @Operation(summary = "캘린더에 월별 일정 목록 조회", description = "캘린더 페이지에서 월별 일정을 조회합니다.")
    @GetMapping("/v1/user/calendar/schedules")
    public ApiResponse<ScheduleMonthlyResDTO> createScheduleAtPost(
            @RequestParam @Parameter int year, @RequestParam @Parameter int month) {
        ScheduleMonthlyResDTO dto = scheduleGetService.getScheduleMonthly(year, month);
        return ApiResponse.response(HttpStatus.OK, SCHEDULE_CALENDAR_READ.getMessage(),dto);
    }


}
