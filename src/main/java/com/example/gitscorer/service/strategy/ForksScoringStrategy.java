package com.example.gitscorer.service.strategy;

import com.example.gitscorer.businessobject.RepositoryDetailBo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static com.example.gitscorer.constants.ScoreCalculatorConstants.MAX_FORKS_NORMALIZED;

/**
 * Implements a scoring strategy based on the number of forks a repository has.
 * The score is normalized using a logarithmic scale to account for the power-law distribution of forks,
 * and then weighted by a configurable factor.
 */
@Slf4j
@Component
public class ForksScoringStrategy implements ScoringStrategy {
    @Value("${weights.forksWeight:1}")
    private Integer forksWeight;

    /**
     * Normalizes the number of forks for a given repository and calculates its weighted score.
     * The normalization uses `log10(forks + 1)` to handle the skewed distribution of fork counts.
     *
     * @param repository The {@link RepositoryDetailBo} containing the repository details, including fork count.
     * @return A double representing the normalized and weighted score based on the number of forks.
     * @throws NullPointerException if the forks count in the repository is null.
     */
    @Override
    public double normalizeAndCalculateScore(RepositoryDetailBo repository) {
        var forks = repository.forks();
        Objects.requireNonNull(forks);

        var normalizedForkScore = Math.log10(forks + 1) / MAX_FORKS_NORMALIZED;
        log.info("Normalized Fork Score: {}", normalizedForkScore);

        return normalizedForkScore * forksWeight;
    }
}
