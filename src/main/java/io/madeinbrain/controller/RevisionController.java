package io.madeinbrain.controller;

import io.madeinbrain.domain.Revision;
import io.madeinbrain.repository.RevisionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/revisions")
@RequiredArgsConstructor
public class RevisionController {

    private final RevisionRepository revisionRepository;

    @GetMapping("/{entityType}/{entityId}")
    public ResponseEntity<List<Revision>> history(@PathVariable String entityType, @PathVariable String entityId) {
        return ResponseEntity.ok(revisionRepository.findByEntityTypeAndEntityIdOrderByCreatedAtAsc(entityType, entityId));
    }
}
