package com.example.gitscorer.controller;

import com.example.gitscorer.datatransferobject.RepositoryRequestDto;
import com.example.gitscorer.exception.GithubServiceUnavailableException;
import com.example.gitscorer.service.RepositoryRetrieverFacade;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
public class PopularRepositoriesExceptionTest {

    @RegisterExtension
    static WireMockExtension wireMock = WireMockExtension.newInstance()
            .options(wireMockConfig().port(8089))
            .build();
    @Autowired
    RepositoryRetrieverFacade repositoryRetrieverFacade;

    @Test
    public void should_fail_gracefully_if_github_is_down() {
        var request = new RepositoryRequestDto(LocalDate.now(), "Java");

        assertThatThrownBy(() -> repositoryRetrieverFacade.getTopNRepositories(request))
                .isInstanceOf(GithubServiceUnavailableException.class)
                .hasMessageContaining("Call to Github has failed! Returning empty response");
    }
}
