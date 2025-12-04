package com.tavemakers.surf.domain.post.repository;

import com.tavemakers.surf.domain.post.entity.Post;
import com.tavemakers.surf.domain.post.entity.Schedule;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findByStartAtBetween(LocalDateTime startOfMonth, LocalDateTime endOfMonth);

    List<Schedule> findByStartAtBetweenAndCategoryIn(
            LocalDateTime startOfMonth,
            LocalDateTime endOfMonth,
            List<String> categories
    );

    Optional<Schedule> findByPostId(Long postId);


    @Query("SELECT s.post FROM Schedule s WHERE s.id = :scheduleId")

    void deleteByPostId(Long postId);
    Post findPostByScheduleId(Long scheduleId);

}