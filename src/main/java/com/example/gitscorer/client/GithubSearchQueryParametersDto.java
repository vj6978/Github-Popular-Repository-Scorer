package com.example.gitscorer.client;

public record GithubSearchQueryParametersDto (
            String q,
            int per_page,
            int page
) {}
