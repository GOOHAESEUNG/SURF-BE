package com.tavemakers.surf.domain.letter.service;

import com.tavemakers.surf.domain.letter.entity.Letter;
import com.tavemakers.surf.domain.letter.repository.LetterRepository;
import com.tavemakers.surf.domain.letter.exception.LetterNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LetterGetService {

    private final LetterRepository letterRepository;

    public Letter getById(Long id) {
        return letterRepository.findById(id)
                .orElseThrow(LetterNotFoundException::new);
    }
}
