package io.madeinbrain.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "revisions", indexes = {
        @Index(columnList = "entityType, entityId")
})
@Data
@NoArgsConstructor @AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class Revision extends BaseEntity {

    private String entityType;

    private String entityId;

    private String actor; // username snapshot

    @Column(length = 8000)
    private String reason;

    @Column(length = 16000)
    private String changeSetJson; // JSON diff of fields

}



