package com.tavemakers.surf.domain.post.service;

import com.tavemakers.surf.domain.member.entity.enums.MemberRole;
import com.tavemakers.surf.domain.post.dto.res.ScheduleMonthlyResDTO;
import com.tavemakers.surf.domain.post.dto.res.ScheduleResDTO;
import com.tavemakers.surf.domain.post.entity.Schedule;
import com.tavemakers.surf.domain.post.exception.ScheduleNotFoundException;
import com.tavemakers.surf.domain.post.repository.ScheduleRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScheduleGetService {
    private final ScheduleRepository scheduleRepository;

    public ScheduleMonthlyResDTO getScheduleMonthly(String memberRole, int year, int month) {
        List<Schedule> schedules = getSchedulesByMonth(memberRole, year, month);
        List<ScheduleResDTO> scheduleResDTOS = getScheduleResDTOs(schedules);
        return getScheduleMonthlyResDTOs(year, month, scheduleResDTOS);
    }

    //특정 월의 일정 조회
    @Transactional(readOnly = true)
    protected List<Schedule> getSchedulesByMonth(String memberRole, int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDateTime startOfMonth = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime endOfMonth = yearMonth.atEndOfMonth().atTime(23, 59, 59);

        List<String> categories = List.of("정규행사", "기타일정");

        if(Objects.equals(memberRole, MemberRole.MEMBER.toString())) {
            return scheduleRepository.findByStartAtBetweenAndCategoryIn(startOfMonth, endOfMonth,categories);
        }else {
            return scheduleRepository.findByStartAtBetween(startOfMonth, endOfMonth);
        }
    }

    //개별 일정 응답 생성
    @Transactional(readOnly = true)
    protected List<ScheduleResDTO> getScheduleResDTOs(List<Schedule> schedules) {
        return ScheduleResDTO.fromEntities(schedules);
    }

    //월별 일정 응답 생성
    @Transactional(readOnly = true)
    protected ScheduleMonthlyResDTO getScheduleMonthlyResDTOs(int year, int month, List<ScheduleResDTO> dto) {
        return ScheduleMonthlyResDTO.of(year,month,dto);
    }

    //아이디로 일정 조회
    @Transactional(readOnly = true)
    public Schedule getScheduleById(Long scheduleId) {
        return scheduleRepository.findById(scheduleId)
                .orElseThrow(ScheduleNotFoundException::new);
    }

    //특정 일정 개별 조회 - 게시글
    @Transactional(readOnly = true)
    public ScheduleResDTO getScheduleSingleDTO(Long postId) {
        Schedule schedule = scheduleRepository.findByPostId(postId)
                .orElseThrow(ScheduleNotFoundException::new);
        return ScheduleResDTO.fromEntity(schedule);
    }
}
