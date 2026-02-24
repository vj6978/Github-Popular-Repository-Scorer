package com.example.gitscorer.controller;

import com.example.gitscorer.datatransferobject.RepositoryRequestDto;
import com.example.gitscorer.exception.GithubServiceUnavailableException;
import com.example.gitscorer.service.RepositoryRetriever;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
public class PopularRepositoriesExceptionTest {

    @RegisterExtension
    static WireMockExtension wireMock = WireMockExtension.newInstance()
            .options(wireMockConfig().port(8089))
            .build();
    @Autowired
    RepositoryRetriever repositoryRetriever;

    @Test
    public void should_fail_gracefully_and_return_empty_list_if_github_is_down() {
        var request = new RepositoryRequestDto(LocalDate.now(), "Java");

        var result = repositoryRetriever.getTopNRepositories(request);

        assertThat(result).isEmpty();
    }
}
