package com.tavemakers.surf.domain.badge.repository;

import com.tavemakers.surf.domain.badge.entity.MemberBadge;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberBadgeRepository extends JpaRepository<MemberBadge, Long> {

    @Query("SELECT mb FROM MemberBadge mb WHERE mb.memberId = :memberId")
    Slice<MemberBadge> findMemberBadgeListByMemberId(@Param("memberId") Long memberId, Pageable pageable);

}
