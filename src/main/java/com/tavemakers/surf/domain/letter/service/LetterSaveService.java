package com.tavemakers.surf.domain.letter.service;

import com.tavemakers.surf.domain.letter.entity.Letter;
import com.tavemakers.surf.domain.letter.repository.LetterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LetterSaveService {

    private final LetterRepository letterRepository;

    @Transactional
    public Letter save(Letter letter) {
        return letterRepository.save(letter);
    }
}
