package com.example.gitscorer.service;

import com.example.gitscorer.businessobject.RepositoryDetailBo;
import com.example.gitscorer.datatransferobject.RepositoryRequestDto;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class ScoringServiceTest {
    @Mock
    private RepositoryRetriever repositoryRetriever;

    @InjectMocks
    private ScoringService scoringService;

    @Test
    public void should_return_top_n_repositories() {
        // GIVEN
        var request = new RepositoryRequestDto(
                LocalDate.parse("2014-01-01"),
                "Java"
        );

        var repositoryBo = new RepositoryDetailBo("Tetris", 1, 2, Instant.now(), 10.0);

        Mockito.when(repositoryRetriever.getTopNRepositories(request)).thenReturn(List.of(repositoryBo));

        List<RepositoryDetailBo> result = scoringService.getPopularRepositories(request);

        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.size()).isEqualTo(1);
        var repository = result.getFirst();
        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(repository.stars()).isEqualTo(repositoryBo.stars());
            softAssertions.assertThat(repository.forks()).isEqualTo(repositoryBo.forks());
            softAssertions.assertThat(repository.mostRecentUpdate()).isEqualTo(repositoryBo.mostRecentUpdate());
            softAssertions.assertThat(repository.score()).isEqualTo(repositoryBo.score());
            softAssertions.assertThat(repository.name()).isEqualTo(repositoryBo.name());
        });
    }
}
