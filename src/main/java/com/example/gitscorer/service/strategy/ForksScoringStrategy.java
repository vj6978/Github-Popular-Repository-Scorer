package com.example.gitscorer.service.strategy;

import com.example.gitscorer.businessobject.RepositoryDetailBo;
import com.example.gitscorer.service.strategy.util.Formulas;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Slf4j
@Component
public class ForksScoringStrategy implements ScoringStrategy {
    private final double forksWeight;

    public ForksScoringStrategy(@Value("${weights.forks:1}") int forksWeight) {
        this.forksWeight = forksWeight;
    }

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

        var formula = Objects.requireNonNull(Formulas.formulaFactory("forks"));

        var normalizedForkScore = formula.apply(repository);
        log.debug("Normalized Fork Score: {}", normalizedForkScore);

        return normalizedForkScore * forksWeight;
    }
}
