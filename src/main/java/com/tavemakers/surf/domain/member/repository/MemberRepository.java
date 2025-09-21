package com.tavemakers.surf.domain.member.repository;

import com.tavemakers.surf.domain.member.entity.Member;
import com.tavemakers.surf.domain.member.entity.enums.MemberStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByIdAndStatus(Long memberId, MemberStatus status);

    boolean existsByEmail(String email);
    Optional<Member> findByEmail(String email);
}
