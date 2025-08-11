package io.madeinbrain.dto;

import io.madeinbrain.entity.Theory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateTheoryRequest {
    private String title;
    private String description;
    private Theory.Category category;
    private List<String> supportingEvidence;
    private List<String> opposingEvidence;
    private List<CreateSourceRequest> sources;
}