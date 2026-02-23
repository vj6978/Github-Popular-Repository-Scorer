package com.example.gitscorer.service.strategy;

import com.example.gitscorer.businessobject.RepositoryDetailBo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

import static com.example.gitscorer.constants.ScoreCalculatorConstants.LAMBDA;

/**
 * Implements a scoring strategy based on the most recent update timestamp of a repository.
 * This strategy uses an exponential decay model to assign higher scores to more recently updated repositories.
 * The decay is controlled by a lambda constant, ensuring scores do not become negative due to inactivity.
 *
 * The formula used is: `Score_{recency} = e^{-\lambda \times \Delta days}`
 * where `\Delta days` is the number of days since the last update.
 */
@Slf4j
@Component
public class MostRecentUpdateScoringStrategy implements ScoringStrategy {

    @Value("${weights.mostRecentUpdateWeight:1}")
    private Integer mostRecentUpdateWeight;

    /**
     * Normalizes the recency of the most recent update for a given repository and calculates its weighted score.
     * The score decays exponentially with the number of days since the last update.
     *
     * @param repository The {@link RepositoryDetailBo} containing the repository details, including the most recent update timestamp.
     * @return A double representing the normalized and weighted score based on the recency of the last update.
     * @throws NullPointerException if the most recent update timestamp in the repository is null.
     */
    @Override
    public double normalizeAndCalculateScore(RepositoryDetailBo repository) {
        var mostRecentUpdate = repository.mostRecentUpdate();
        Objects.requireNonNull(mostRecentUpdate);

        var daysToMostRecentUpdate = ChronoUnit.DAYS.between(mostRecentUpdate, Instant.now());
        var normalizedMostRecentUpdateScore = Math.exp(LAMBDA * daysToMostRecentUpdate);

        log.info("Normalized Most Recent Update Score: {}", normalizedMostRecentUpdateScore);

        return normalizedMostRecentUpdateScore * mostRecentUpdateWeight;
    }
}
