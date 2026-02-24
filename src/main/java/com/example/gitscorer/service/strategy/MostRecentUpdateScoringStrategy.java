package com.example.gitscorer.service.strategy;

import com.example.gitscorer.businessobject.RepositoryDetailBo;
import com.example.gitscorer.service.strategy.util.Formulas;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * Implements a scoring strategy based on the most recent update timestamp of a repository.
 * This strategy uses an exponential decay model to assign higher scores to more recently updated repositories.
 * The decay is controlled by a lambda constant, ensuring scores do not become negative due to inactivity.
 * <p>
 * The formula used is: `Score_{recency} = e^{-\lambda \times \Delta days}`
 * where `\Delta days` is the number of days since the last update.
 */
@Slf4j
@Component
public class MostRecentUpdateScoringStrategy implements ScoringStrategy {
    private final int mostRecentUpdateWeight;

    public MostRecentUpdateScoringStrategy(@Value("${weights.mostRecentUpdates:1}") int mostRecentUpdateWeight) {
        this.mostRecentUpdateWeight = mostRecentUpdateWeight;
    }

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
        var formula = Objects.requireNonNull(Formulas.formulaFactory("mostRecentUpdate"));

        var normalizedMostRecentUpdateScore = formula.apply(repository);

        log.debug("Normalized Most Recent Update Score: {}", normalizedMostRecentUpdateScore);

        return normalizedMostRecentUpdateScore * mostRecentUpdateWeight;
    }
}
