package io.madeinbrain.dto;

import io.madeinbrain.entity.Source;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SourceDTO {
    private Long id;
    private String title;
    private Source.SourceType type;
    private String author;
    private Integer year;
    private Double reliability;
    private String url;
    private String doi;
}