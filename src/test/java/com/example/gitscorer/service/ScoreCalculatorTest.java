package com.example.gitscorer.service;

import com.example.gitscorer.businessobject.RepositoryDetailBo;
import com.example.gitscorer.service.strategy.ForksScoringStrategy;
import com.example.gitscorer.service.strategy.MostRecentUpdateScoringStrategy;
import com.example.gitscorer.service.strategy.ScoringStrategy;
import com.example.gitscorer.service.strategy.StarsScoringStrategy;
import com.example.gitscorer.service.strategy.util.Formulas;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.text.Normalizer;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

import static com.example.gitscorer.constants.ScoreCalculatorConstants.MAX_FORKS_NORMALIZED;
import static com.example.gitscorer.constants.ScoreCalculatorConstants.MAX_STARS_NORMALIZED;

@ExtendWith(MockitoExtension.class)
public class ScoreCalculatorTest {
    private List<ScoringStrategy> strategies = List.of(new StarsScoringStrategy(1), new ForksScoringStrategy(1), new MostRecentUpdateScoringStrategy(1));

    private ScoreCalculator scoreCalculator = new ScoreCalculator(strategies);

    @Test
    public void should_run_all_normalization_strategies_and_assign_repository_score() {
        var request = new RepositoryDetailBo(
                "Tetris", 1, 2, Instant.now(), 10.0
        );

        var starsFormula = Objects.requireNonNull(Formulas.formulaFactory("stars"));
        var starsScore = 1 * starsFormula.apply(request);
        var forksFormula = Objects.requireNonNull(Formulas.formulaFactory("forks"));
        var forksScore = 1 * forksFormula.apply(request);
        var mostRecentUpdateFormula = Objects.requireNonNull(Formulas.formulaFactory("mostRecentUpdate"));
        var mostRecentUpdateScore = 1 * mostRecentUpdateFormula.apply(request);

        var result = scoreCalculator.calculateAndUpdateWithScore(request);

        Assertions.assertThat(result.score()).isEqualTo(starsScore + forksScore + mostRecentUpdateScore);
    }
}
