package com.example.gitscorer.datatransferobject;

import java.time.Instant;

public record RepositoryInfoResponseDto(
        String name,
        Integer stars,
        Integer forks,
        Instant mostRecentUpdate,
        Double score
) {
}
