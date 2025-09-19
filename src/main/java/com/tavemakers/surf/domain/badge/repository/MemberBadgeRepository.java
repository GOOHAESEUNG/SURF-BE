package com.tavemakers.surf.domain.badge.repository;

import com.tavemakers.surf.domain.badge.entity.MemberBadge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberBadgeRepository extends JpaRepository<MemberBadge, Long> {
}
