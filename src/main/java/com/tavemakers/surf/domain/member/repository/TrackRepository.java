package com.tavemakers.surf.domain.member.repository;

import com.tavemakers.surf.domain.member.entity.Track;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TrackRepository extends JpaRepository<Track, Long> {

    //특정 memberId를 가진 트랙들을 generation(기수) 기준으로 내림차순 정렬하여
    //가장 첫 번째(최신) 트랙을 조회합니다.
    Optional<Track> findTopByMemberIdOrderByGenerationDesc(Long memberId);

    // Track을 조회할 때 연관된 현재 활동 중인 Member도 함께 조회하여 N+1 문제를 방지
    @Query("SELECT t FROM Track t JOIN FETCH t.member m WHERE m.activityStatus = true")
    List<Track> findAllWithActiveMember();
}
