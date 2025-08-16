package io.madeinbrain.dto;

import io.madeinbrain.domain.DomainType;
import lombok.*;

import java.util.Set;
import java.util.UUID;

public class TheoryDtos {

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class CreateTheoryRequest {
        private String title;
        private String description;
        private DomainType domain;
        private Set<String> tags;
    }

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class UpdateTheoryRequest {
        private String title;
        private String description;
        private DomainType domain;
        private Set<String> tags;
        private String reason;
    }

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class TheoryResponse {
        private UUID id;
        private String title;
        private String description;
        private DomainType domain;
        private Set<String> tags;
        private boolean locked;
        private double currentConfidence;
    }
}
