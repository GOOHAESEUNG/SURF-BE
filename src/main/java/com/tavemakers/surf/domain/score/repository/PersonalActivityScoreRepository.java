package com.tavemakers.surf.domain.score.repository;

import com.tavemakers.surf.domain.score.entity.PersonalActivityScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PersonalActivityScoreRepository extends JpaRepository<PersonalActivityScore, Long> {

    Optional<PersonalActivityScore> findByMemberId(Long memberId);

}
