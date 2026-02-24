package com.example.gitscorer.service;

import com.example.gitscorer.businessobject.RepositoryDetailBo;
import com.example.gitscorer.client.GithubFeignClient;
import com.example.gitscorer.client.GithubSearchQueryParametersDto;
import com.example.gitscorer.datatransferobject.RepositoryRequestDto;
import com.example.gitscorer.datatransferobject.githubfeignclient.GithubApiResponseDto;
import com.example.gitscorer.exception.GithubServiceUnavailableException;
import com.example.gitscorer.mapper.RepositoryMapper;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Function;

@Slf4j
@Service
@RequiredArgsConstructor
public class GithubApiFacade {
    private final GithubFeignClient githubFeignClient;
    private final RepositoryMapper mapper;
    private final ScoreCalculator scoreCalculator;

    private final Function<RepositoryRequestDto, String> languageAndEarliestCreatedDate =
            request -> "language:" +
                    request.repositoryLanguage() +
                    " created:<" +
                    request.earliestCreatedDate();

    @Cacheable(value = "topRepositories", key = "#request.earliestCreatedDate + '-' + #request.repositoryLanguage")
    public List<RepositoryDetailBo> getTopNRepositories(int pageLimit, RepositoryRequestDto request) {
        int RESULTS_PER_PAGE = 100;
        List<RepositoryDetailBo> repositories = new ArrayList<>();
        Map<Integer, Future<GithubApiResponseDto>> resultFutures = new HashMap<>();

        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int pageNumber = 1; pageNumber <= pageLimit; pageNumber++) {
                var queryParameters = new GithubSearchQueryParametersDto(
                        languageAndEarliestCreatedDate.apply(request),
                        RESULTS_PER_PAGE,
                        pageNumber
                );
                resultFutures.put(pageNumber, executor.submit(() -> githubFeignClient.getRepositories(queryParameters)));
            }
        }

        for (int key : resultFutures.keySet()) {
            try {
                var response = resultFutures.get(key).get();
                log.debug("Github API response was {}", response);
                var scoreUpdatedResponse = response
                        .items()
                        .stream()
                        .map(mapper::toBo)
                        .map(scoreCalculator::calculateAndUpdateWithScore)
                        .toList();
                repositories.addAll(scoreUpdatedResponse);
            } catch (Exception ex) {
                Throwable cause = ex.getCause();
                if (cause instanceof FeignException.InternalServerError) {
                    log.warn("Retrieving repositories for page {} failed! Returning empty list!", key);
                } else if (ex instanceof FeignException.TooManyRequests) {
                    throw new GithubServiceUnavailableException("Github rate limit exceeded! Try again later!");
                } else {
                    log.error("Github call failed for unknown reason {}", cause.getMessage());
                }
            }
        }
        return repositories;
    }
}
