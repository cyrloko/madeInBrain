package io.madeinbrain.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor @AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class UserAccount extends BaseEntity {

    @Column(unique = true, nullable = false)
    private String username;

    @Enumerated(EnumType.STRING)
    private Role role;

    private double reputation;

    private Instant lastActiveAt;
}
