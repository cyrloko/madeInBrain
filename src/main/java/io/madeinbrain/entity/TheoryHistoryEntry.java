package io.madeinbrain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "theory_history")
@EntityListeners(AuditingEntityListener.class)
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TheoryHistoryEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theory_id", nullable = false)
    private Theory theory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActionType actionType;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private Double previousConfidenceScore;

    @Column(nullable = false)
    private Double newConfidenceScore;

    @CreatedDate
    private LocalDateTime createdAt;

    public enum ActionType {
        CREATED, UPDATED, SOURCE_ADDED, SOURCE_REMOVED, EVIDENCE_ADDED, EVIDENCE_REMOVED, EXPERT_VALIDATION
    }
}