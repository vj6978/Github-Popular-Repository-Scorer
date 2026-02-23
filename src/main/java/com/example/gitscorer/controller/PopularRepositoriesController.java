package com.example.gitscorer.controller;

import com.example.gitscorer.datatransferobject.RepositoryInfoResponseDto;
import com.example.gitscorer.datatransferobject.RepositoryRequestDto;
import com.example.gitscorer.mapper.RepositoryMapper;
import com.example.gitscorer.service.ScoringService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PopularRepositoriesController {
    private final ScoringService scoringService;
    private final RepositoryMapper mapper;

    @PostMapping("/repositories/popular")
    public ResponseEntity<List<RepositoryInfoResponseDto>> getPopularRepositories(@Valid @RequestBody RepositoryRequestDto request) {
        log.info("Fetching most popular repositories with criteria {}", request.toString());
        List<RepositoryInfoResponseDto> response = scoringService.getPopularRepositories(request).stream().map(mapper::toDto).toList();
        return ResponseEntity.ok().body(response);
    }
}
