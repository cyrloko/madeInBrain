package io.madeinbrain.dto;

import io.madeinbrain.domain.SourceType;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

public class SourceDtos {

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class CreateSourceRequest {
        private String url;
        private String title;
        private SourceType type;
        private String publisher;
        private Instant publishedAt;
        private boolean peerReviewed;
    }

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class SourceResponse {
        private UUID id;
        private String url;
        private String title;
        private SourceType type;
        private String publisher;
        private Instant publishedAt;
        private boolean peerReviewed;
        private double intrinsicReliability;
    }
}
