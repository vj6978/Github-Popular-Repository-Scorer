package com.example.gitscorer.datatransferobject;

import java.time.LocalDate;

public record RepositoryRequestDto(LocalDate earliestCreatedDate, String repositoryLanguage) { }
