package io.madeinbrain.repository;

import io.madeinbrain.domain.Source;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SourceRepository extends JpaRepository<Source, UUID> {
    Optional<Source> findByUrl(String url);
}
