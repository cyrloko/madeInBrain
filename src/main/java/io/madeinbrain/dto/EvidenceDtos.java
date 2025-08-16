package io.madeinbrain.dto;

import io.madeinbrain.domain.Stance;
import lombok.*;

import java.util.UUID;

public class EvidenceDtos {

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class AddEvidenceRequest {
        private UUID theoryId;
        private UUID sourceId;
        private Stance stance;
        private String notes;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class EvidenceResponse {
        private UUID id;
        private UUID theoryId;
        private UUID sourceId;
        private Stance stance;
        private double weight;
        private String notes;
    }
}
