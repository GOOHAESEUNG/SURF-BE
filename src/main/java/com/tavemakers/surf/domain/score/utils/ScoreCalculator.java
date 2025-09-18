package com.tavemakers.surf.domain.score.utils;

import com.tavemakers.surf.domain.score.entity.ScoreComputable;
import org.springframework.stereotype.Component;

@Component
public class ScoreCalculator {

    public double calculateScore(ScoreComputable scoreComputable, double delta) {
        return scoreComputable.updateScore(delta);
    }

}
