package com.example.gitscorer.service;

import com.example.gitscorer.businessobject.RepositoryDetailBo;
import com.example.gitscorer.datatransferobject.RepositoryRequestDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
public class RepositoryRetriever {
    private final int pageLimit;
    private final int topNToReturn;
    private final GithubApiFacade githubApiFacade;

    public RepositoryRetriever(@Value("${topNToReturn:10}") int topNToReturn, @Value("${pageLimit:3}") int pageLimit, GithubApiFacade githubApiFacade) {
        this.pageLimit = pageLimit;
        this.topNToReturn = topNToReturn;
        this.githubApiFacade = githubApiFacade;
    }

    public List<RepositoryDetailBo> getTopNRepositories(RepositoryRequestDto request) {
        final PriorityQueue<RepositoryDetailBo> popularReposQueue = new PriorityQueue<>(topNToReturn, Comparator.comparingDouble(RepositoryDetailBo::score));

        List<RepositoryDetailBo> repositories = githubApiFacade.getTopNRepositories(pageLimit, request);

        log.debug("Fetched {} repositories: [{}]", repositories.size(), repositories);

        if (repositories.isEmpty()) {
            log.debug("No repositories found! Returning empty list!");
            return Collections.emptyList();
        }

        repositories.forEach(repository -> {
            popularReposQueue.offer(repository);
            if (popularReposQueue.size() > topNToReturn) {
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
