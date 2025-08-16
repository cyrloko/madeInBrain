package io.madeinbrain.dto;

import lombok.*;

import java.time.Instant;
import java.util.List;

public class ScoreDtos {

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class ConfidenceBreakdown {
        private double total;
        private double evidenceComponent;
        private double consensusComponent;
        private double sourceQualityComponent;
        private String algorithmVersion;
        private String explanation;
    }

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class SnapshotPoint {
        private Instant at;
        private double score;
    }

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class HistoryResponse {
        private List<SnapshotPoint> history;
    }
}
