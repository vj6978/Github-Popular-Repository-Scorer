package com.example.gitscorer.service;

import com.example.gitscorer.businessobject.RepositoryDetailBo;
import com.example.gitscorer.datatransferobject.RepositoryRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScoringService {
    private final RepositoryRetrieverFacade repositoryRetrieverFacade;

    public List<RepositoryDetailBo> getPopularRepositories(RepositoryRequestDto request) {
        log.debug("Requesting Github for repositories in {} by earliest created date {}", request.repositoryLanguage(), request.earliestCreatedDate());
        List<RepositoryDetailBo> topNRepositories = repositoryRetrieverFacade.getTopNRepositories(request);
        log.info("Top N Repositories were {}", topNRepositories);
        return topNRepositories;
    }
}
