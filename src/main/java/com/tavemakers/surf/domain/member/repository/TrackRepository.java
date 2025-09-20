package com.tavemakers.surf.domain.member.repository;

import com.tavemakers.surf.domain.member.entity.Track;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TrackRepository extends JpaRepository<Track, Long> {

    Optional<Track> findByMemberId(Long memberId);

    // Track을 조회할 때 연관된 Member도 함께 조회하여 N+1 문제를 방지
    @Query("SELECT t FROM Track t JOIN FETCH t.member")
    List<Track> findAllWithMember();
}
