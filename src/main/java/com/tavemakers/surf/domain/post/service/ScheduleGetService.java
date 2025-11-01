package com.tavemakers.surf.domain.post.service;

import com.tavemakers.surf.domain.post.dto.res.ScheduleMonthlyResDTO;
import com.tavemakers.surf.domain.post.dto.res.ScheduleResDTO;
import com.tavemakers.surf.domain.post.entity.Schedule;
import com.tavemakers.surf.domain.post.repository.ScheduleRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScheduleGetService {
    private final ScheduleRepository scheduleRepository;

    public ScheduleMonthlyResDTO getScheduleMonthly(int year, int month) {
        List<Schedule> schedules = getSchedulesByMonth(year, month);
        List<ScheduleResDTO> scheduleResDTOS = getScheduleResDTOs(schedules);
        return getScheduleMonthlyResDTOs(year, month, scheduleResDTOS);
    }

    //특정 월의 일정 조회
    private List<Schedule> getSchedulesByMonth(int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDateTime startOfMonth = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime endOfMonth = yearMonth.atEndOfMonth().atTime(23, 59, 59);

        return scheduleRepository.findByStartAtBetween(startOfMonth, endOfMonth);
    }

    //개별 일정 응답 생성
    private List<ScheduleResDTO> getScheduleResDTOs(List<Schedule> schedules) {
        return ScheduleResDTO.fromEntities(schedules);
    }

    //월별 일정 응답 생성
    private ScheduleMonthlyResDTO getScheduleMonthlyResDTOs(int year, int month, List<ScheduleResDTO> dto) {
        return ScheduleMonthlyResDTO.of(year,month,dto);
    }
}
