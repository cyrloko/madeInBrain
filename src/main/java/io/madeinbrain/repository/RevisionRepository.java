package io.madeinbrain.repository;

import io.madeinbrain.domain.Revision;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RevisionRepository extends JpaRepository<Revision, UUID> {
    List<Revision> findByEntityTypeAndEntityIdOrderByCreatedAtAsc(String entityType, String entityId);
}
