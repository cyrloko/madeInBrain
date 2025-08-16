package io.madeinbrain.dto;

import lombok.*;

import java.util.UUID;

public class VoteDtos {

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class CastVoteRequest {
        private UUID theoryId;
        private double score; // -1 to +1
        private String rationale;
    }

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class VoteResponse {
        private UUID id;
        private UUID theoryId;
        private String voter;
        private double rawScore;
        private double weightedScore;
        private String rationale;
    }
}
