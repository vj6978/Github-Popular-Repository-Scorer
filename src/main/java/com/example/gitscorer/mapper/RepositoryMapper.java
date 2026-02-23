package com.example.gitscorer.mapper;

import com.example.gitscorer.businessobject.RepositoryDetailBo;
import com.example.gitscorer.datatransferobject.RepositoryInfoResponseDto;
import com.example.gitscorer.datatransferobject.githubfeignclient.RepositoryDetailDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface RepositoryMapper {
    RepositoryInfoResponseDto toDto(RepositoryDetailBo repositoryDetailBo);

    @Mapping(target = "score", ignore = true)
    RepositoryDetailBo toBo(RepositoryDetailDto repositoryDetailDto);
}
