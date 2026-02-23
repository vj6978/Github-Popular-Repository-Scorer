package com.example.gitscorer.client;

import com.example.gitscorer.datatransferobject.githubfeignclient.GithubApiResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "github-api-client", url = "https://api.github.com")
public interface GithubFeignClient {
    @GetMapping(
            value = "/search/repositories",
            headers = {
                    "X-GitHub-Api-Version=2022-11-28",
                    "Accept=application/vnd.github+json",
                    "User-Agent=GitScorer-App"
            }
    )
    GithubApiResponseDto getRepositories(
            @SpringQueryMap GithubSearchQueryParametersDto queryParametersDto
    );
}
