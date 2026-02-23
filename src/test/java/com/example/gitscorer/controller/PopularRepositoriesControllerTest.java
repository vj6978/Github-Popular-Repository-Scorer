package com.example.gitscorer.controller;

import com.example.gitscorer.businessobject.RepositoryDetailBo;
import com.example.gitscorer.datatransferobject.RepositoryInfoResponseDto;
import com.example.gitscorer.datatransferobject.RepositoryRequestDto;
import com.example.gitscorer.mapper.RepositoryMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import com.example.gitscorer.service.ScoringService;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PopularRepositoriesController.class)
public class PopularRepositoriesControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private RepositoryMapper mapper;

    @MockitoBean
    private ScoringService scoringService;

    @DisplayName("""
            GIVEN: Request for popular github repositories
            WHEN: Repositories are queried
            THEN: A list of repositories is returned with successful status
            """)
    @Test
    public void happy_path_test() throws Exception {
        var request = new RepositoryRequestDto(
                LocalDate.now(),
                "Java"
        );

        var repositoryBo = new RepositoryDetailBo("Tetris", 1, 1, Instant.now(), 1.0);

        var repositoryResponseDto = new RepositoryInfoResponseDto("Tetris", 1, 1, Instant.now(), 1.0);

        Mockito.when(scoringService.getPopularRepositories(request)).thenReturn(List.of(repositoryBo));
        Mockito.when(mapper.toDto(repositoryBo)).thenReturn(repositoryResponseDto);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/repositories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.forks").value(1));
    }
}
