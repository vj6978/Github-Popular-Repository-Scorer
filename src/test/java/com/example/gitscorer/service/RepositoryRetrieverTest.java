package com.example.gitscorer.service;

import com.example.gitscorer.businessobject.RepositoryDetailBo;
import com.example.gitscorer.datatransferobject.RepositoryRequestDto;
import com.example.gitscorer.datatransferobject.githubfeignclient.RepositoryDetailDto;
import com.example.gitscorer.mapper.RepositoryMapper;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RepositoryRetrieverTest {
    private final RepositoryMapper mapper = mock(RepositoryMapper.class);
    private final ScoreCalculator scoreCalculator = mock(ScoreCalculator.class);
    private final GithubApiFacade githubApiFacade = mock(GithubApiFacade.class);

    RepositoryRetriever facade = new RepositoryRetriever(
            2,
            1,
            githubApiFacade
    );

    @Test
    public void should_return_top_n_repositories() {
        var request = new RepositoryRequestDto(
                LocalDate.parse("2024-01-01"),
                "Java"
        );

        var lombokRepository = new RepositoryDetailDto("Lombok", 1, 2, Instant.now());
        var zookeeperRepository = new RepositoryDetailDto("Zookeeper", 2, 4, Instant.now());
        var lombokBo = new RepositoryDetailBo("Lombok", 1, 2, Instant.now(), 7.0);
        var zookeperBo = new RepositoryDetailBo("Zookeeper", 2, 4, Instant.now(), 12.0);

        when(githubApiFacade.getTopNRepositories(anyInt(), any())).thenReturn(List.of(lombokBo, zookeperBo));
        when(mapper.toBo(lombokRepository)).thenReturn(lombokBo);
        when(mapper.toBo(zookeeperRepository)).thenReturn(zookeperBo);
        when(scoreCalculator.calculateAndUpdateWithScore(lombokBo)).thenReturn(lombokBo);
        when(scoreCalculator.calculateAndUpdateWithScore(zookeperBo)).thenReturn(zookeperBo);

        var response = facade.getTopNRepositories(request);

        Assertions.assertThat(response).isNotNull();
        SoftAssertions.assertSoftly(softAssertions -> {
            var result = response.getFirst();
            softAssertions.assertThat(result.name()).isEqualTo(zookeperBo.name());
            softAssertions.assertThat(result.stars()).isEqualTo(zookeperBo.stars());
            softAssertions.assertThat(result.forks()).isEqualTo(zookeperBo.forks());
            softAssertions.assertThat(result.mostRecentUpdate()).isEqualTo(zookeperBo.mostRecentUpdate());
            softAssertions.assertThat(result.score()).isEqualTo(zookeperBo.score());
        });
    }
}
