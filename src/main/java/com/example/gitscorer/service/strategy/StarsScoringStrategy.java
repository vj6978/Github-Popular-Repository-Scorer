package com.example.gitscorer.service.strategy;

import com.example.gitscorer.businessobject.RepositoryDetailBo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static com.example.gitscorer.constants.ScoreCalculatorConstants.MAX_STARS_NORMALIZED;

/**
 * Implements a scoring strategy based on the number of stars a repository has.
 * The score is normalized using a logarithmic scale to account for the power-law distribution of stars,
 * and then weighted by a configurable factor.
 *
 * The normalization formula used is: `log10(stars + 1) / MAX_STARS_NORMALIZED`.
 * This approach helps to give a more normalized value due to the power-law distribution of star counts.
 */
@Slf4j
@Component
public class StarsScoringStrategy implements ScoringStrategy {

    @Value("${weights.starsWeight:1}")
    private Integer starsWeight;

    /**
     * Normalizes the number of stars for a given repository and calculates its weighted score.
     * The normalization uses `log10(stars + 1)` to handle the skewed distribution of star counts.
     *
     * @param repository The {@link RepositoryDetailBo} containing the repository details, including star count.
     * @return A double representing the normalized and weighted score based on the number of stars.
     * @throws NullPointerException if the stars count in the repository is null.
     */
    @Override
    public double normalizeAndCalculateScore(RepositoryDetailBo repository) {
        var stars = repository.stars();
        Objects.requireNonNull(stars);

        var normalizedStarsScore =  Math.log10(stars + 1) / MAX_STARS_NORMALIZED;
        log.info("Normalized Stars Score: {}", normalizedStarsScore);

        return normalizedStarsScore * starsWeight;
    }
}
