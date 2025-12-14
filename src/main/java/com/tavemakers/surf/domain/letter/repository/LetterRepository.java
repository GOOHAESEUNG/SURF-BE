package com.tavemakers.surf.domain.letter.repository;

import com.tavemakers.surf.domain.letter.entity.Letter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LetterRepository extends JpaRepository<Letter, Long> {
    Slice<Letter> findBySenderId(Long senderId, Pageable pageable);
}
