package com.example.gitscorer.service;

import com.example.gitscorer.service.strategy.ScoringStrategy;
import com.example.gitscorer.businessobject.RepositoryDetailBo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScoreCalculator {

    private final List<ScoringStrategy> scoringStrategies;

    public RepositoryDetailBo calculateAndUpdateWithScore(RepositoryDetailBo repository) {
        log.info("Scoring repository {}", repository.toString());

       double repositoryScore = scoringStrategies
               .stream()
               .mapToDouble(strategy -> strategy.normalizeAndCalculateScore(repository))
               .sum();

        log.info("Repository Score is {}", repositoryScore);

        return new RepositoryDetailBo(repository.name(), repository.stars(), repository.forks(), repository.mostRecentUpdate(), repositoryScore);
    }
}
