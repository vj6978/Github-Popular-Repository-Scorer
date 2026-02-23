package com.example.gitscorer.service.strategy;

import com.example.gitscorer.businessobject.RepositoryDetailBo;

/**
 * Defines the contract for different scoring strategies used to calculate a normalized score for a repository.
 * Each strategy focuses on a specific metric (e.g., stars, forks, recency of update).
 */
public interface ScoringStrategy {

    /**
     * Normalizes a specific metric of a given repository and calculates its weighted score.
     *
     * @param repository The {@link RepositoryDetailBo} containing the repository details.
     * @return A double representing the normalized and weighted score for the specific metric.
     */
    double normalizeAndCalculateScore(RepositoryDetailBo repository);
}
