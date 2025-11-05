package com.tavemakers.surf.domain.post.repository;

import com.tavemakers.surf.domain.post.entity.Schedule;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findByStartAtBetween(LocalDateTime startOfMonth, LocalDateTime endOfMonth);
}
