package com.tavemakers.surf.domain.member.repository;

import com.tavemakers.surf.domain.member.entity.Track;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TrackRepository extends JpaRepository<Track, Long> {

    Optional<Track> findByMemberId(Long memberId);
}
