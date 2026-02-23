package com.example.gitscorer.service.strategy;

import com.example.gitscorer.businessobject.RepositoryDetailBo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static com.example.gitscorer.constants.ScoreCalculatorConstants.MAX_FORKS_NORMALIZED;

@Slf4j
@Component
public class ForksScoringStrategy implements ScoringStrategy {
    @Value("${weights.forksWeight:1}")
    private Integer forksWeight;

    @Override
    public double normalizeAndCalculateScore(RepositoryDetailBo repository) {
        var forks = repository.forks();
        Objects.requireNonNull(forks);

        var normalizedForkScore = Math.log10(forks + 1) / MAX_FORKS_NORMALIZED;
        log.info("Normalized Fork Score: {}", normalizedForkScore);

        return normalizedForkScore * forksWeight;
    }
}
