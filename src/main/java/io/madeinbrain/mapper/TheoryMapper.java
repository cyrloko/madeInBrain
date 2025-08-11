package io.madeinbrain.mapper;

import io.madeinbrain.dto.TheoryDTO;
import io.madeinbrain.entity.Theory;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TheoryMapper {
    TheoryMapper INSTANCE = Mappers.getMapper(TheoryMapper.class);

    TheoryDTO toTheoryDTO(Theory theory);
    Theory toTheory(TheoryDTO theoryDTO);
}
