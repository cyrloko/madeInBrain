package io.madeinbrain.repository;

import io.madeinbrain.entity.Theory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TheoryRepository extends JpaRepository<Theory, Long> {

    Page<Theory> findByCategory(Theory.Category category, Pageable pageable);

    @Query("SELECT t FROM Theory t WHERE " +
            "LOWER(t.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(t.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Theory> findByTitleOrDescriptionContaining(@Param("searchTerm") String searchTerm, Pageable pageable);

    @Query("SELECT t FROM Theory t WHERE " +
            "t.category = :category AND " +
            "(LOWER(t.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(t.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<Theory> findByCategoryAndSearchTerm(@Param("category") Theory.Category category,
                                             @Param("searchTerm") String searchTerm, Pageable pageable);

    List<Theory> findTop10ByOrderByConfidenceScoreDesc();

    List<Theory> findTop10ByOrderByContributorsDesc();

    @Query("SELECT t FROM Theory t WHERE t.confidenceScore >= :minScore ORDER BY t.confidenceScore DESC")
    List<Theory> findByMinimumConfidenceScore(@Param("minScore") Double minScore);
}