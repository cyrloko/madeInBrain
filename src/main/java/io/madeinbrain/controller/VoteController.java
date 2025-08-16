// src/main/java/com/madeinbrain/controller/VoteController.java
package io.madeinbrain.controller;

import io.madeinbrain.domain.WeightedVote;
import io.madeinbrain.dto.VoteDtos;
import io.madeinbrain.service.TheoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/votes")
@RequiredArgsConstructor
public class VoteController {

    private final TheoryService theoryService;

    @PostMapping
    @PreAuthorize("hasAnyRole('USER','EXPERT','ADMIN')")
    public ResponseEntity<VoteDtos.VoteResponse> cast(@RequestBody VoteDtos.CastVoteRequest req) {
        WeightedVote v = theoryService.castVote(req);
        return ResponseEntity.ok(VoteDtos.VoteResponse.builder()
                .id(v.getId())
                .theoryId(v.getTheory().getId())
                .voter(v.getVoter().getUsername())
                .rawScore(v.getRawScore())
                .weightedScore(v.getWeightedScore())
                .rationale(v.getRationale())
                .build());
    }
}
