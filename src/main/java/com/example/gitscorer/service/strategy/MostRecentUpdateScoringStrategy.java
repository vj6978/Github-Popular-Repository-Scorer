package com.example.gitscorer.service.strategy;

import com.example.gitscorer.businessobject.RepositoryDetailBo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

import static com.example.gitscorer.constants.ScoreCalculatorConstants.LAMBDA;

/***
 * Using exponential decay for time, so as to not run into a scenario where subtracting a fixed number
 * point for each unit time of inactivity eventually leads to negative scores. The negative sign indicates decay
 * and not growth.
 * Lambda is the constant. After ln(Half life in days), a repository's recency score drops by 50%.
 * $$Score_{recency} = e^{-\lambda \times \Delta days}$$
 * @param
 * @return
 */

@Slf4j
@Component
public class MostRecentUpdateScoringStrategy implements ScoringStrategy {

    @Value("${weights.mostRecentUpdateWeight:1}")
    private Integer mostRecentUpdateWeight;

    @Override
    public double normalizeAndCalculateScore(RepositoryDetailBo repository) {
        var mostRecentUpdate = repository.mostRecentUpdate();
        Objects.requireNonNull(mostRecentUpdate);

        var daysToMostRecentUpdate = ChronoUnit.DAYS.between(mostRecentUpdate, Instant.now());
        var normalizedMostRecentUpdateScore = Math.exp(LAMBDA * daysToMostRecentUpdate);

        log.info("Normalized Fork Score: {}", normalizedMostRecentUpdateScore);

        return normalizedMostRecentUpdateScore * mostRecentUpdateWeight;
    }
}
