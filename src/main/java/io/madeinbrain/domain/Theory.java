package io.madeinbrain.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@Entity
@Table(name = "theories", indexes = {
        @Index(columnList = "title")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class Theory extends BaseEntity {

    @Column(nullable = false)
    private String title;

    @Column(length = 8000)
    private String description;

    @Enumerated(EnumType.STRING)
    private DomainType domain;

    @ElementCollection
    @CollectionTable(name = "theory_tags", joinColumns = @JoinColumn(name = "theory_id"))
    @Column(name = "tag")
    private Set<String> tags;

    private boolean locked; // cannot be modified unilaterally

    private double currentConfidence; // latest computed score
}
