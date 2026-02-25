package com.example.gitscorer.controller;

import com.example.gitscorer.datatransferobject.RepositoryInfoResponseDto;
import com.example.gitscorer.datatransferobject.RepositoryRequestDto;
import com.example.gitscorer.mapper.RepositoryMapper;
import com.example.gitscorer.service.ScoringService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
public class PopularRepositoriesController {
    private final RepositoryMapper mapper;
    private final ScoringService scoringService;

    @Operation(summary = "Get Top Repositories", description = "Fetches the highest scoring repositories based on stars, forks, and recency.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the list"),
            @ApiResponse(responseCode = "400", description = "Invalid date or language provided"),
            @ApiResponse(responseCode = "503", description = "Could not reach Github")
    })
    @PostMapping("repositories/popular")
    public ResponseEntity<List<RepositoryInfoResponseDto>> getPopularRepositories(@Valid @RequestBody RepositoryRequestDto request) {
        log.info("Fetching most popular repositories with criteria {}", request.toString());
        List<RepositoryInfoResponseDto> response = scoringService.getPopularRepositories(request).stream().map(mapper::toDto).toList();
        return ResponseEntity.ok().body(response);
    }
}
