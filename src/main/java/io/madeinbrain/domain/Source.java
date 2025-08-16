package io.madeinbrain.domain;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.Instant;


@Entity
@Table(name = "sources", indexes = {
        @Index(columnList = "url", unique = true)
})
@Data
@NoArgsConstructor @AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class Source extends BaseEntity {

    @Column(nullable = false, length = 2048)
    private String url;

    private String title;

    @Enumerated(EnumType.STRING)
    private SourceType type;

    private String publisher;

    private Instant publishedAt;

    private boolean peerReviewed;

    private double intrinsicReliability; // precomputed based on type/publisher metadata
}


