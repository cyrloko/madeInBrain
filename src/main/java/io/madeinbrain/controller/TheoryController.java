package io.madeinbrain.controller;

import io.madeinbrain.domain.Theory;
import io.madeinbrain.dto.TheoryDtos;
import io.madeinbrain.repository.TheoryRepository;
import io.madeinbrain.service.TheoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/theories")
@RequiredArgsConstructor
public class TheoryController {

    private final TheoryService theoryService;
    private final TheoryRepository theoryRepository;

    @PostMapping
    @PreAuthorize("hasAnyRole('USER','EXPERT','ADMIN')")
    public ResponseEntity<TheoryDtos.TheoryResponse> create(@RequestBody TheoryDtos.CreateTheoryRequest req) {
        Theory t = theoryService.create(req);
        return ResponseEntity.ok(toResp(t));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','EXPERT','ADMIN')")
    public ResponseEntity<TheoryDtos.TheoryResponse> update(@PathVariable UUID id, @RequestBody TheoryDtos.UpdateTheoryRequest req) {
        Theory t = theoryService.update(id, req);
        return ResponseEntity.ok(toResp(t));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TheoryDtos.TheoryResponse> get(@PathVariable UUID id) {
        Theory t = theoryRepository.findById(id).orElseThrow();
        return ResponseEntity.ok(toResp(t));
    }

    private static TheoryDtos.TheoryResponse toResp(Theory t) {
        return TheoryDtos.TheoryResponse.builder()
                .id(t.getId())
                .title(t.getTitle())
                .description(t.getDescription())
                .domain(t.getDomain())
                .tags(t.getTags())
                .locked(t.isLocked())
                .currentConfidence(t.getCurrentConfidence())
                .build();
    }
}
