package com.tavemakers.surf.domain.member.repository;

import com.tavemakers.surf.domain.member.entity.Career;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CareerRepository extends JpaRepository<Career,Long> {
}
