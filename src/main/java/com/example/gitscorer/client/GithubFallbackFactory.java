package com.example.gitscorer.client;

import com.example.gitscorer.datatransferobject.githubfeignclient.GithubApiResponseDto;
import com.example.gitscorer.exception.GithubServiceUnavailableException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class GithubFallbackFactory implements FallbackFactory<GithubFeignClient> {
    @Override
    public GithubFeignClient create(Throwable cause) {
        return queryParametersDto -> {
            throw new GithubServiceUnavailableException("Call to Github has failed! Returning empty response! Reason was " + cause.getCause() + " with message " + cause.getLocalizedMessage());
        };
    }
}
