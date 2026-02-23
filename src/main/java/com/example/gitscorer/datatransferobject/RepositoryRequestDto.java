package com.example.gitscorer.datatransferobject;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDate;

public record RepositoryRequestDto(
        @NotNull(message = "Earliest Created Date cannot be null")
        @PastOrPresent(message = "A valid date in the past or present in format YYYY-MM-DD")
        LocalDate earliestCreatedDate,

        @NotNull(message = "Language cannot be null")
        @NotBlank(message = "Language cannot be empty")
        String repositoryLanguage
) { }
