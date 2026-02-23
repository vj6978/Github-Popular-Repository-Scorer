package com.example.gitscorer.service;

import com.example.gitscorer.businessobject.RepositoryDetailBo;
import com.example.gitscorer.client.GithubFeignClient;
import com.example.gitscorer.client.GithubSearchQueryParametersDto;
import com.example.gitscorer.datatransferobject.RepositoryRequestDto;
import com.example.gitscorer.datatransferobject.githubfeignclient.GithubApiResponseDto;
import com.example.gitscorer.datatransferobject.githubfeignclient.RepositoryDetailDto;
import com.example.gitscorer.mapper.RepositoryMapper;
import com.example.gitscorer.mapper.RepositoryMapperImpl;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RepositoryRetrieverFacadeTest {
    private final RepositoryMapper mapper = mock(RepositoryMapper.class);
    private final ScoreCalculator scoreCalculator = mock(ScoreCalculator.class);
    private final GithubFeignClient githubFeignClient = mock(GithubFeignClient.class);

    RepositoryRetrieverFacade facade = new RepositoryRetrieverFacade(
            1,
            2,
            mapper,
            scoreCalculator,
            githubFeignClient
    );

    @Test
    public void should_return_top_n_repositories() {
        var request = new RepositoryRequestDto(
                LocalDate.parse("2024-01-01"),
                "Java"
        );

        var queryParameters = new GithubSearchQueryParametersDto(
                "language:" +
                        request.repositoryLanguage() +
                        " created:<" +
                        request.earliestCreatedDate(),
                100,
                1
        );

        var lombokRepository = new RepositoryDetailDto("Lombok", 1, 2, Instant.now());
        var zookeeperRepository = new RepositoryDetailDto("Zookeeper", 2, 4, Instant.now());
        var lombokBo = new RepositoryDetailBo("Lombok", 1, 2, Instant.now(), 7.0);
        var zookeperBo = new RepositoryDetailBo("Zookeeper", 2, 4, Instant.now(), 12.0);
        var githubResponse = new GithubApiResponseDto(List.of(lombokRepository, zookeeperRepository));

        when(githubFeignClient.getRepositories(any())).thenReturn(githubResponse);
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

    @Test
    public void should_handle_partial_failure_gracefully() {
        var request = new RepositoryRequestDto(
                LocalDate.parse("2024-01-01"),
                "Java"
        );

        var firstPageQueryParameters = new GithubSearchQueryParametersDto(
                "language:" +
                        request.repositoryLanguage() +
                        " created:<" +
                        request.earliestCreatedDate(),
                100,
                1
        );

        var secondPageQueryParameters = new GithubSearchQueryParametersDto(
                "language:" +
                        request.repositoryLanguage() +
                        " created:<" +
                        request.earliestCreatedDate(),
                100,
                2
        );

        var lombokRepository = new RepositoryDetailDto("Lombok", 1, 2, Instant.now());
        var zookeeperRepository = new RepositoryDetailDto("Zookeeper", 2, 4, Instant.now());
        var lombokBo = new RepositoryDetailBo("Lombok", 1, 2, Instant.now(), 7.0);
        var zookeperBo = new RepositoryDetailBo("Zookeeper", 2, 4, Instant.now(), 12.0);
        var firstPageGitHubResponse = new GithubApiResponseDto(List.of(lombokRepository));

        when(githubFeignClient.getRepositories(firstPageQueryParameters)).thenReturn(firstPageGitHubResponse);
        when(githubFeignClient.getRepositories(secondPageQueryParameters)).thenThrow(new RuntimeException("Failed to fetch repositories!"));

        when(mapper.toBo(lombokRepository)).thenReturn(lombokBo);
        when(mapper.toBo(zookeeperRepository)).thenReturn(zookeperBo);
        when(scoreCalculator.calculateAndUpdateWithScore(lombokBo)).thenReturn(lombokBo);
        when(scoreCalculator.calculateAndUpdateWithScore(zookeperBo)).thenReturn(zookeperBo);

        var response = facade.getTopNRepositories(request);

        Assertions.assertThat(response).isNotNull();
        SoftAssertions.assertSoftly(softAssertions -> {
            var result = response.getFirst();
            softAssertions.assertThat(result.name()).isEqualTo(lombokBo.name());
            softAssertions.assertThat(result.stars()).isEqualTo(lombokBo.stars());
            softAssertions.assertThat(result.forks()).isEqualTo(lombokBo.forks());
            softAssertions.assertThat(result.mostRecentUpdate()).isEqualTo(lombokBo.mostRecentUpdate());
            softAssertions.assertThat(result.score()).isEqualTo(lombokBo.score());
        });
    }

}
