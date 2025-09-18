package com.tavemakers.surf.domain.score.service;

import com.tavemakers.surf.domain.score.entity.PersonalActivityScore;
import com.tavemakers.surf.domain.score.exception.PersonalScoreNotFoundException;
import com.tavemakers.surf.domain.score.repository.PersonalActivityScoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PersonalScoreGetService {

    private final PersonalActivityScoreRepository personalScoreRepository;

    public PersonalActivityScore getPersonalScore(Long memberId) {
        return personalScoreRepository.findByMemberId(memberId)
                .orElseThrow(PersonalScoreNotFoundException::new);
    }

}
