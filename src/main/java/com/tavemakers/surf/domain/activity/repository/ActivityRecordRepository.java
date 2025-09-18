package com.tavemakers.surf.domain.activity.repository;

import com.tavemakers.surf.domain.activity.entity.ActivityRecord;
import com.tavemakers.surf.domain.activity.entity.enums.ScoreType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityRecordRepository extends JpaRepository<ActivityRecord, Long> {

    @Query("SELECT ar " +
            "FROM ActivityRecord ar " +
            "WHERE ar.memberId = :memberId " +
            "AND ar.isDeleted = false " +
            "AND ar.scoreType = :scoreType")
    Slice<ActivityRecord> findActivityRecordListByMemberId(
            @Param("memberId") Long memberId,
            @Param("scoreType") ScoreType scoreType,
            Pageable pageable
    );

}