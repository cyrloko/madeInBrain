package io.madeinbrain.controller;

import io.madeinbrain.domain.Theory;
import io.madeinbrain.dto.ScoreDtos;
import io.madeinbrain.repository.TheoryRepository;
import io.madeinbrain.service.HistoryService;
import io.madeinbrain.service.ScoringService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/scores")
@RequiredArgsConstructor
public class ScoreController {

    private final ScoringService scoringService;
    private final HistoryService historyService;
    private final TheoryRepository theoryRepository;

    @PostMapping("/recompute/{theoryId}")
    public ResponseEntity<ScoreDtos.ConfidenceBreakdown> recompute(@PathVariable UUID theoryId) {
        Theory t = theoryRepository.findById(theoryId).orElseThrow();
        var snap = scoringService.recompute(t);
        return ResponseEntity.ok(ScoreDtos.ConfidenceBreakdown.builder()
                .total(snap.getScore())
                .evidenceComponent(snap.getEvidenceComponent())
                .consensusComponent(snap.getConsensusComponent())
                .sourceQualityComponent(snap.getSourceQualityComponent())
                .algorithmVersion(snap.getAlgorithmVersion())
                .explanation(snap.getExplanation())
                .build());
    }

    @GetMapping("/history/{theoryId}")
    public ResponseEntity<ScoreDtos.HistoryResponse> history(@PathVariable UUID theoryId) {
        Theory t = theoryRepository.findById(theoryId).orElseThrow();
        return ResponseEntity.ok(historyService.getHistory(t));
    }
}
