package io.madeinbrain.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

@Entity
@Table(name = "confidence_snapshots", indexes = {
        @Index(columnList = "theory_id, capturedAt")
})
@Data
@NoArgsConstructor @AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class ConfidenceSnapshot extends BaseEntity {

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Theory theory;

    private Instant capturedAt;

    private double score;

    private double evidenceComponent;
    private double consensusComponent;
    private double sourceQualityComponent;

    @Column(length = 2000)
    private String algorithmVersion; // for transparency

    @Column(length = 4000)
    private String explanation; // human-readable breakdown
}
