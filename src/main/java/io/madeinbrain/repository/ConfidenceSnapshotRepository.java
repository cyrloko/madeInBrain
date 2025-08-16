// src/main/java/com/madeinbrain/repo/ConfidenceSnapshotRepository.java
package io.madeinbrain.repository;

import io.madeinbrain.domain.ConfidenceSnapshot;
import io.madeinbrain.domain.Theory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ConfidenceSnapshotRepository extends JpaRepository<ConfidenceSnapshot, UUID> {
    List<ConfidenceSnapshot> findByTheoryOrderByCapturedAtAsc(Theory theory);
}
