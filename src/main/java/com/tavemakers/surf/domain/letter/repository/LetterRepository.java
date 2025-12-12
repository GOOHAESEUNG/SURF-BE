package com.tavemakers.surf.domain.letter.repository;

import com.tavemakers.surf.domain.letter.entity.Letter;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LetterRepository extends JpaRepository<Letter, Long> {
}
