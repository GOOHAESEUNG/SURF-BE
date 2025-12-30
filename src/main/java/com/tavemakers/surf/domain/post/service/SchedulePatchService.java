package com.tavemakers.surf.domain.post.service;

import com.tavemakers.surf.domain.post.dto.req.ScheduleUpdateReqDTO;
import com.tavemakers.surf.domain.post.entity.Post;
import com.tavemakers.surf.domain.post.entity.Schedule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SchedulePatchService {

    //일정 수정
    @Transactional
    public void updateSchedule(Schedule schedule, ScheduleUpdateReqDTO dto) {
        schedule.updateSchedule(dto);
    }

    //일정 존재 여부 변경
    @Transactional
    public void updateHasScheduleTrue(Post post, boolean hasSchedule) {
        post.changeHasSchedule(hasSchedule);
    }
}
