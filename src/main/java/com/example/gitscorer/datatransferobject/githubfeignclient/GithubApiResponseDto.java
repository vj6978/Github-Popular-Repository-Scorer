package com.example.gitscorer.datatransferobject.githubfeignclient;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GithubApiResponseDto(List<RepositoryDetailDto> items) {}


