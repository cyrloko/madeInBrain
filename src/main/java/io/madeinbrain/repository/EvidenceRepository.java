package io.madeinbrain.repository;

import io.madeinbrain.domain.Evidence;
import io.madeinbrain.domain.Theory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface EvidenceRepository extends JpaRepository<Evidence, UUID> {
    List<Evidence> findByTheory(Theory theory);
}
