package io.madeinbrain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "theories")
@EntityListeners(AuditingEntityListener.class)
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Theory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String title;

    @Column(nullable = false, length = 2000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    @Column(nullable = false)
    @Builder.Default
    private Double confidenceScore = 0.0;

    @Column(nullable = false)
    @Builder.Default
    private Integer contributors = 0;

    @Column(nullable = false)
    @Builder.Default
    private Integer expertValidations = 0;

    @ElementCollection
    @CollectionTable(name = "theory_supporting_evidence")
    @Builder.Default
    private List<String> supportingEvidence = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "theory_opposing_evidence")
    @Builder.Default
    private List<String> opposingEvidence = new ArrayList<>();

    @OneToMany(mappedBy = "theory", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Source> sources = new ArrayList<>();

    @OneToMany(mappedBy = "theory", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<TheoryHistoryEntry> history = new ArrayList<>();

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime lastUpdated;

    public enum Category {
        SCIENCE, MEDICINE, POLITICS, ECONOMICS, PSYCHOLOGY, TECHNOLOGY, ENVIRONMENT
    }
}