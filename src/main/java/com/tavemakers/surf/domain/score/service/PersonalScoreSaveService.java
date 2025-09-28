package com.tavemakers.surf.domain.score.service;

import com.tavemakers.surf.domain.member.entity.Member;
import com.tavemakers.surf.domain.score.entity.PersonalActivityScore;
import com.tavemakers.surf.domain.score.repository.PersonalActivityScoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PersonalScoreSaveService {

    private final PersonalActivityScoreRepository personalScoreRepository;

    public void savePersonalScore(Member savedMember) {
        personalScoreRepository.save(PersonalActivityScore.of(savedMember));
    }

}
