package io.madeinbrain.controller;

import io.madeinbrain.domain.Source;
import io.madeinbrain.dto.SourceDtos;
import io.madeinbrain.repository.SourceRepository;
import io.madeinbrain.service.TheoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/sources")
@RequiredArgsConstructor
public class SourceController {

    private final TheoryService theoryService;
    private final SourceRepository sourceRepository;

    @PostMapping
    @PreAuthorize("hasAnyRole('USER','EXPERT','ADMIN')")
    public ResponseEntity<SourceDtos.SourceResponse> upsert(@RequestBody SourceDtos.CreateSourceRequest req) {
        Source s = theoryService.createOrUpdateSource(req);
        return ResponseEntity.ok(toResp(s));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SourceDtos.SourceResponse> get(@PathVariable UUID id) {
        Source s = sourceRepository.findById(id).orElseThrow();
        return ResponseEntity.ok(toResp(s));
    }

    private static SourceDtos.SourceResponse toResp(Source s) {
        return SourceDtos.SourceResponse.builder()
                .id(s.getId())
                .url(s.getUrl())
                .title(s.getTitle())
                .type(s.getType())
                .publisher(s.getPublisher())
                .publishedAt(s.getPublishedAt())
                .peerReviewed(s.isPeerReviewed())
                .intrinsicReliability(s.getIntrinsicReliability())
                .build();
    }
}
