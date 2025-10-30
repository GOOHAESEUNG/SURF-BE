package com.tavemakers.surf.domain.reservation.service;

import com.tavemakers.surf.domain.reservation.task.PostPublishRunner;
import com.tavemakers.surf.domain.reservation.task.PostPublishTask;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationScheduleService {

    private final PostPublishRunner taskRunner;
    private final TaskScheduler taskScheduler;

    public void schedule(Long reservationId, Instant publishAt) {
        PostPublishTask task = PostPublishTask.of(reservationId, taskRunner);
        taskScheduler.schedule(task, publishAt);
    }

}
