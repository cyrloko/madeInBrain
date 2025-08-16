package io.madeinbrain.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "weighted_votes", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"theory_id", "voter_id"})
})
@Data
@NoArgsConstructor @AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class WeightedVote extends BaseEntity {

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Theory theory;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "voter_id")
    private UserAccount voter;

    private double rawScore; // e.g., -1..+1 or 0..1 depending on scheme
    private double reputationAtTime; // snapshot
    private double weightedScore; // computed from reputation & trust signals

    @Column(length = 1000)
    private String rationale;
}
