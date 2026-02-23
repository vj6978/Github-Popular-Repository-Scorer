package com.example.gitscorer.businessobject;

import java.time.Instant;

public record RepositoryDetailBo(
        String name,
        Integer stars,
        Integer forks,
        Instant mostRecentUpdate,
        Double score
) {
}
