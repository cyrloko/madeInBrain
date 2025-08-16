package io.madeinbrain.repository;

import io.madeinbrain.domain.Theory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TheoryRepository extends JpaRepository<Theory, UUID> {
}
