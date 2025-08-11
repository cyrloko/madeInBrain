package io.madeinbrain.repository;

import io.madeinbrain.entity.Source;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SourceRepository extends JpaRepository<Source, Long> {

    List<Source> findByTheoryId(Long theoryId);

    List<Source> findByType(Source.SourceType type);

    @Query("SELECT s FROM Source s WHERE s.reliability >= :minReliability ORDER BY s.reliability DESC")
    List<Source> findByMinimumReliability(@Param("minReliability") Double minReliability);

    @Query("SELECT s FROM Source s WHERE s.theory.id = :theoryId ORDER BY s.reliability DESC")
    List<Source> findByTheoryIdOrderByReliabilityDesc(@Param("theoryId") Long theoryId);
}