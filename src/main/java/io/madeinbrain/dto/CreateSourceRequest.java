package io.madeinbrain.dto;

import io.madeinbrain.entity.Source;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateSourceRequest {
    private String title;
    private Source.SourceType type;
    private String author;
    private Integer year;
    private Double reliability;
    private String url;
    private String doi;
}
