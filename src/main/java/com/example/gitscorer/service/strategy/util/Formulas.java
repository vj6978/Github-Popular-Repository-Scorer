package com.example.gitscorer.service.strategy.util;

import com.example.gitscorer.businessobject.RepositoryDetailBo;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.function.Function;

import static com.example.gitscorer.constants.ScoreCalculatorConstants.*;

@Slf4j
public final class Formulas {

    private static Map<String, Function<RepositoryDetailBo, Double>> strategies = Map.of(
            "stars", repositoryDetailBo -> Math.log10(repositoryDetailBo.stars() + 1) / MAX_STARS_NORMALIZED,
            "forks", repositoryDetailBo -> Math.log10(repositoryDetailBo.forks() + 1) / MAX_FORKS_NORMALIZED,
            "mostRecentUpdate", repositoryDetailBo -> {
                var daysToMostRecentUpdate = ChronoUnit.DAYS.between(repositoryDetailBo.mostRecentUpdate(), Instant.now());
                return Math.exp(LAMBDA * daysToMostRecentUpdate);
            }
    );

    public static Function<RepositoryDetailBo, Double> formulaFactory(String metric) {
        try {
            return strategies.get(metric);
        } catch (NullPointerException ex) {
            log.debug("No formula exists for metric {}", metric);
            return null;
        }
    }
}
