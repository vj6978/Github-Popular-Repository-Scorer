package com.example.gitscorer.datatransferobject.githubfeignclient;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

@JsonIgnoreProperties(ignoreUnknown = true)
public record RepositoryDetailDto(
        String name,
        @JsonProperty("stargazers_count") Integer stars,
        @JsonProperty("forks_count") Integer forks,
        @JsonProperty("updated_at") Instant mostRecentUpdate
) { }
