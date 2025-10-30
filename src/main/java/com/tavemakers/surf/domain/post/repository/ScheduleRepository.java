package com.tavemakers.surf.domain.post.repository;

import com.tavemakers.surf.domain.post.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
}
