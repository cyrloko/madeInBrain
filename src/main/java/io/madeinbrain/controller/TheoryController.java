package io.madeinbrain.controller;

import io.madeinbrain.dto.CreateTheoryRequest;
import io.madeinbrain.dto.TheoryDTO;
import io.madeinbrain.entity.Source;
import io.madeinbrain.entity.Theory;
import io.madeinbrain.entity.User;
import io.madeinbrain.service.TheoryService;
import io.madeinbrain.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/theories")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class TheoryController {

    private TheoryService theoryService;

    private UserService userService;

    @GetMapping
    public ResponseEntity<Page<TheoryDTO>> getAllTheories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "confidenceScore") String sortBy) {

        Page<TheoryDTO> theories = theoryService.getAllTheories(page, size, sortBy);
        return ResponseEntity.ok(theories);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<TheoryDTO>> searchTheories(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Theory.Category category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<TheoryDTO> theories = theoryService.searchTheories(q, category, page, size);
        return ResponseEntity.ok(theories);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TheoryDTO> getTheoryById(@PathVariable Long id) {
        return theoryService.getTheoryById(id)
                .map(theory -> ResponseEntity.ok().body(theory))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<TheoryDTO> createTheory(@RequestBody CreateTheoryRequest request, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User user = userService.findByUsername(principal.getName())
                .orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            TheoryDTO theory = theoryService.createTheory(request, user);
            return ResponseEntity.status(HttpStatus.CREATED).body(theory);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<TheoryDTO> updateTheory(@PathVariable Long id,
                                                  @RequestBody CreateTheoryRequest request,
                                                  Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User user = userService.findByUsername(principal.getName())
                .orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        //TODO: A corriger
        return theoryService.updateTheory(id, request, user, new Source())
                .map(theory -> ResponseEntity.ok().body(theory))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTheory(@PathVariable Long id, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User user = userService.findByUsername(principal.getName())
                .orElse(null);
        if (user == null || (!user.getRole().equals(User.Role.ADMIN) && !user.getRole().equals(User.Role.MODERATOR))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        if (theoryService.deleteTheory(id)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/top/confidence")
    public ResponseEntity<List<TheoryDTO>> getTopTheoriesByConfidence(
            @RequestParam(defaultValue = "10") int limit) {

        List<TheoryDTO> theories = theoryService.getAllTheories(1,limit, "size").toList();
        return ResponseEntity.ok(theories);
    }

    @GetMapping("/top/contributors")
    public ResponseEntity<List<TheoryDTO>> getTopTheoriesByContributors(
            @RequestParam(defaultValue = "10") int limit) {

        //List<TheoryDTO> theories = theoryService.getTopTheoriesByContributors(limit);
        List<TheoryDTO> theories = theoryService.getAllTheories(1,limit, "size").toList();
        return ResponseEntity.ok(theories);
    }

    @GetMapping("/categories")
    public ResponseEntity<Theory.Category[]> getCategories() {
        return ResponseEntity.ok(Theory.Category.values());
    }
}