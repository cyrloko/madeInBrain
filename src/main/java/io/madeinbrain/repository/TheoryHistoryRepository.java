package io.madeinbrain.repository;

import io.madeinbrain.entity.TheoryHistoryEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TheoryHistoryRepository extends JpaRepository<TheoryHistoryEntry, Long> {

    Page<TheoryHistoryEntry> findByTheoryIdOrderByCreatedAtDesc(Long theoryId, Pageable pageable);

    Page<TheoryHistoryEntry> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
}