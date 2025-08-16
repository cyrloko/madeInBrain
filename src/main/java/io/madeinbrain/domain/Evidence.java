package io.madeinbrain.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "evidence", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"theory_id", "source_id"})
})
@Data
@NoArgsConstructor @AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class Evidence extends BaseEntity {

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Theory theory;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Source source;

    @Enumerated(EnumType.STRING)
    private Stance stance;

    private double weight; // system-assigned weight for this evidence given quality etc.

    @Column(length = 4000)
    private String notes;
}
