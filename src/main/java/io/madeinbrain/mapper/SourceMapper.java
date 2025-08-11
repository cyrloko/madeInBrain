package io.madeinbrain.mapper;

import io.madeinbrain.dto.SourceDTO;
import io.madeinbrain.entity.Source;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SourceMapper {
    SourceMapper INSTANCE = Mappers.getMapper(SourceMapper.class);

    SourceDTO toSourceDTO(Source source);
    Source toSource(SourceDTO sourceDTO);
}
