// src/main/java/com/madeinbrain/service/ScoringService.java
package io.madeinbrain.service;

import io.madeinbrain.domain.*;
import io.madeinbrain.repository.ConfidenceSnapshotRepository;
import io.madeinbrain.repository.EvidenceRepository;
import io.madeinbrain.repository.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScoringService {

    private final EvidenceRepository evidenceRepository;
    private final VoteRepository voteRepository;
    private final ConfidenceSnapshotRepository snapshotRepository;

    private static final String ALGO_VERSION = "MIB-1.0";

    public ConfidenceSnapshot recompute(Theory theory) {
        List<Evidence> evidence = evidenceRepository.findByTheory(theory);
        List<WeightedVote> votes = voteRepository.findByTheory(theory);

        double sourceQualityComponent = evidence.stream()
                .mapToDouble(e -> e.getSource().getIntrinsicReliability())
                .average().orElse(0.5);

        double evidenceSupport = evidence.stream()
                .mapToDouble(e -> {
                    double s = switch (e.getStance()) {
                        case SUPPORTS -> +1.0;
                        case REFUTES -> -1.0;
                        case NEUTRAL -> 0.0;
                    };
                    return s * e.getWeight();
                }).sum();

        double evidenceComponent = sigmoid(evidenceSupport / Math.max(1, evidence.size())); // normalize

        double consensusRaw = votes.stream().mapToDouble(WeightedVote::getWeightedScore).average().orElse(0.0);
        double consensusComponent = (consensusRaw + 1.0) / 2.0; // map -1..+1 -> 0..1

        // weighted aggregation with transparency-friendly weights
        double total = clamp(0.25 * sourceQualityComponent + 0.5 * evidenceComponent + 0.25 * consensusComponent);

        var snap = ConfidenceSnapshot.builder()
                .theory(theory)
                .capturedAt(Instant.now())
                .score(total)
                .evidenceComponent(evidenceComponent)
                .consensusComponent(consensusComponent)
                .sourceQualityComponent(sourceQualityComponent)
                .algorithmVersion(ALGO_VERSION)
                .explanation("score=0.25*source+0.5*evidence+0.25*consensus; normalized & clamped")
                .createdAt(ZonedDateTime.now().toInstant())
                .build();

        snapshotRepository.save(snap);
        theory.setCurrentConfidence(total);
        return snap;
    }

    private static double sigmoid(double x) {
        return 1.0 / (1.0 + Math.exp(-4.0 * x));
    }

    private static double clamp(double v) {
        return Math.max(0.0, Math.min(1.0, v));
    }

    // Periodic safety net recomputation
    @Scheduled(fixedDelay = 15 * 60 * 1000L)
    public void periodicRecompute() {
        // In real code, iterate all theories; omitted here to keep the skeleton lightweight.
    }
}
