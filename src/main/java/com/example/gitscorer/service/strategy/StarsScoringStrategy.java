package com.example.gitscorer.service.strategy;

import com.example.gitscorer.businessobject.RepositoryDetailBo;
import com.example.gitscorer.service.strategy.util.Formulas;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Slf4j
@Component
public class StarsScoringStrategy implements ScoringStrategy {

    private final int starsWeight;

    public StarsScoringStrategy(@Value("${weights.stars:1}") int starsWeight) {
        this.starsWeight = starsWeight;
    }

    /**
     * Normalizes the number of stars for a given repository and calculates its weighted score.
     * The normalization uses `log10(stars + 1)` to handle the skewed distribution of star counts.
     *
     * @param repository The {@link RepositoryDetailBo} containing the repository details, including star count.
     * @return A double representing the normalized and weighted score based on the number of stars.
     * @throws NullPointerException if the stars count in the repository is null.
     */
    @Override
    public double normalizeAndCalculateScore(RepositoryDetailBo repository) {
        var stars = repository.stars();
        Objects.requireNonNull(stars);
        var formula = Objects.requireNonNull(Formulas.formulaFactory("stars"));

        var normalizedStarsScore = formula.apply(repository);
        log.info("Normalized Stars Score: {}", normalizedStarsScore);

        return normalizedStarsScore * starsWeight;
    }
}
