package com.example.gitscorer.service.strategy;

import com.example.gitscorer.businessobject.RepositoryDetailBo;

public interface ScoringStrategy {
    double normalizeAndCalculateScore(RepositoryDetailBo repository);
}
