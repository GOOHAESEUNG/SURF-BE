package com.tavemakers.surf.domain.activity.repository;

import com.tavemakers.surf.domain.activity.entity.ActivityRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActivityRecordRepository extends JpaRepository<ActivityRecord, Long> {
}