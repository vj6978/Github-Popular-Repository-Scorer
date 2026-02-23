package com.example.gitscorer.service;

import com.example.gitscorer.businessobject.RepositoryDetailBo;
import com.example.gitscorer.client.GithubFeignClient;
import com.example.gitscorer.client.GithubSearchQueryParametersDto;
import com.example.gitscorer.datatransferobject.RepositoryRequestDto;
import com.example.gitscorer.mapper.RepositoryMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.stereotype.Component;

import javax.naming.ServiceUnavailableException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.IntStream;

@Slf4j
@Component
public class RepositoryRetrieverFacade {
    private final int pageLimit;
    private final int topNToReturn;

    private final RepositoryMapper mapper;
    private final ScoreCalculator scoreCalculator;
    private final GithubFeignClient githubFeignClient;
    private final SimpleAsyncTaskExecutor asyncTaskExecutor = new SimpleAsyncTaskExecutor();

    public RepositoryRetrieverFacade(@Value("${topNToReturn:10}") int topNToReturn, @Value("${pageLimit:3}") int pageLimit, RepositoryMapper mapper, ScoreCalculator scoreCalculator, GithubFeignClient githubFeignClient) {
        this.topNToReturn = topNToReturn;
        this.pageLimit = pageLimit;
        this.mapper = mapper;
        this.scoreCalculator = scoreCalculator;
        this.githubFeignClient = githubFeignClient;

        asyncTaskExecutor.setVirtualThreads(true);
    }

    private final Function<RepositoryRequestDto, String> languageAndEarliestCreatedDate =
            request -> "language:" +
                    request.repositoryLanguage() +
                    " created:<" +
                    request.earliestCreatedDate();

    public List<RepositoryDetailBo> getTopNRepositories(RepositoryRequestDto request) {
        int RESULTS_PER_PAGE = 100;

        final PriorityQueue<RepositoryDetailBo> popularReposQueue = new PriorityQueue<>(topNToReturn, Comparator.comparingDouble(RepositoryDetailBo::score));

        List<CompletableFuture<List<RepositoryDetailBo>>> repositoryFutures = IntStream.rangeClosed(1, pageLimit)
                .mapToObj(page -> CompletableFuture.supplyAsync(() -> {
                                    var queryParameters = new GithubSearchQueryParametersDto(
                                            languageAndEarliestCreatedDate.apply(request),
                                            RESULTS_PER_PAGE,
                                            page
                                    );
                                    return githubFeignClient.getRepositories(queryParameters).items()
                                            .stream()
                                            .map(mapper::toBo)
                                            .map(scoreCalculator::calculateAndUpdateWithScore)
                                            .toList();
                                }, asyncTaskExecutor)
                                .exceptionally(exception -> {
                                    log.info("Retrieving repositories for page {} failed! Returning empty list!", page);
                                    return Collections.emptyList();
                                })
                )
                .toList();

        List<RepositoryDetailBo> repositories = repositoryFutures
                .stream()
                .map(CompletableFuture::join)
                .flatMap(List::stream)
                .toList();

        log.info("Fetched {} repositories: [{}]", repositories.size(), repositories);

        if (repositories.isEmpty()) {
            log.info("No repositories found!");
            return Collections.emptyList();
        }

        repositories.forEach(repository -> {
            popularReposQueue.offer(repository);
            if (popularReposQueue.size() > topNToReturn) {
                log.info("{} repositories collected! Polling to rebalance", topNToReturn);
                popularReposQueue.poll();
            }
        });

        List<RepositoryDetailBo> sortedList = new ArrayList<>();
        while (!popularReposQueue.isEmpty()) {
            sortedList.add(popularReposQueue.poll());
        }
        Collections.reverse(sortedList);
        return sortedList;
    }
}
