package com.tavemakers.surf.domain.feedback.repository;

import com.tavemakers.surf.domain.feedback.entity.Feedback;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    long countByWriterHash(String writerHash);

    Page<Feedback> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
