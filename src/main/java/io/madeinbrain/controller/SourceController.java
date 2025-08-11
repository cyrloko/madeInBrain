package io.madeinbrain.controller;

import io.madeinbrain.dto.CreateSourceRequest;
import io.madeinbrain.dto.SourceDTO;
import io.madeinbrain.entity.Source;
import io.madeinbrain.entity.User;
import io.madeinbrain.service.SourceService;
import io.madeinbrain.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/sources")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class SourceController {

    @Autowired
    private SourceService sourceService;

    @Autowired
    private UserService userService;

    @GetMapping("/theory/{theoryId}")
    public ResponseEntity<List<SourceDTO>> getSourcesByTheoryId(@PathVariable Long theoryId) {
        List<SourceDTO> sources = sourceService.getSourcesByTheoryId(theoryId);
        return ResponseEntity.ok(sources);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SourceDTO> getSourceById(@PathVariable Long id) {
        return sourceService.getSourceById(id)
                .map(source -> ResponseEntity.ok().body(source))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/theory/{theoryId}")
    public ResponseEntity<SourceDTO> addSourceToTheory(@PathVariable Long theoryId,
                                                       @RequestBody CreateSourceRequest request,
                                                       Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User user = userService.findByUsername(principal.getName())
                .orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return sourceService.addSourceToTheory(theoryId, request, user)
                .map(source -> ResponseEntity.status(HttpStatus.CREATED).body(source))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSource(@PathVariable Long id, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User user = userService.findByUsername(principal.getName())
                .orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (sourceService.deleteSource(id, user)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<SourceDTO>> getSourcesByType(@PathVariable Source.SourceType type) {
        List<SourceDTO> sources = sourceService.getSourcesByType(type);
        return ResponseEntity.ok(sources);
    }

    @GetMapping("/reliable")
    public ResponseEntity<List<SourceDTO>> getHighReliabilitySources(
            @RequestParam(defaultValue = "90.0") double minReliability) {

        List<SourceDTO> sources = sourceService.getHighReliabilitySources(minReliability);
        return ResponseEntity.ok(sources);
    }

    @GetMapping("/types")
    public ResponseEntity<Source.SourceType[]> getSourceTypes() {
        return ResponseEntity.ok(Source.SourceType.values());
    }
}