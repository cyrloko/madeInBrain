package io.madeinbrain.dto;

import io.madeinbrain.entity.Theory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TheoryDTO {
    private Long id;
    private String title;
    private String description;
    private Theory.Category category;
    private Double confidenceScore;
    private Integer contributors;
    private Integer expertValidations;
    private List<String> supportingEvidence;
    private List<String> opposingEvidence;
    private List<SourceDTO> sources;
    private LocalDateTime lastUpdated;
}