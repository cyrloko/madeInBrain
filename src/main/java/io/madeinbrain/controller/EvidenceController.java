// src/main/java/com/madeinbrain/controller/EvidenceController.java
package io.madeinbrain.controller;

import io.madeinbrain.domain.Evidence;
import io.madeinbrain.dto.EvidenceDtos;
import io.madeinbrain.repository.EvidenceRepository;
import io.madeinbrain.service.TheoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/evidence")
@RequiredArgsConstructor
public class EvidenceController {

    private final TheoryService theoryService;
    private final EvidenceRepository evidenceRepository;

    @PostMapping
    @PreAuthorize("hasAnyRole('USER','EXPERT','ADMIN')")
    public ResponseEntity<EvidenceDtos.EvidenceResponse> add(@RequestBody EvidenceDtos.AddEvidenceRequest req) {
        Evidence e = theoryService.addEvidence(req);
        return ResponseEntity.ok(toResp(e));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EvidenceDtos.EvidenceResponse> get(@PathVariable UUID id) {
        Evidence e = evidenceRepository.findById(id).orElseThrow();
        return ResponseEntity.ok(toResp(e));
    }

    private static EvidenceDtos.EvidenceResponse toResp(Evidence e) {
        return EvidenceDtos.EvidenceResponse.builder()
                .id(e.getId())
                .theoryId(e.getTheory().getId())
                .sourceId(e.getSource().getId())
                .stance(e.getStance())
                .weight(e.getWeight())
                .notes(e.getNotes())
                .build();
    }
}
