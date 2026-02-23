package com.example.gitscorer.service.strategy;

import com.example.gitscorer.businessobject.RepositoryDetailBo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static com.example.gitscorer.constants.ScoreCalculatorConstants.MAX_STARS_NORMALIZED;

/***
 * Most starred Github Repo -> ~ 500k stars -> codecrafters-io/build-your-own-x
 * normalized(stars) = log10(stars + 1)/log10(maxstars + 1) -> Due to power-law distribution log gives a more normalized value
 */

@Slf4j
@Component
public class StarsScoringStrategy implements ScoringStrategy {

    @Value("${weights.starsWeight:1}")
    private Integer starsWeight;

    @Override
    public double normalizeAndCalculateScore(RepositoryDetailBo repository) {
        var stars = repository.stars();
        Objects.requireNonNull(stars);

        var normalizedStarsScore =  Math.log10(stars + 1) / MAX_STARS_NORMALIZED;
        log.info("Normalized Stars Score: {}", normalizedStarsScore);

        return normalizedStarsScore * starsWeight;
    }
}
