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
@Table(name = "sources")
@EntityListeners(AuditingEntityListener.class)
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Source {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 1000)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SourceType type;

    @Column(nullable = false, length = 500)
    private String author;

    @Column(nullable = false)
    private Integer year;

    @Column(nullable = false)
    private Double reliability;

    @Column(length = 2000)
    private String url;

    @Column(length = 50)
    private String doi;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theory_id", nullable = false)
    private Theory theory;

    @CreatedDate
    private LocalDateTime createdAt;

    public enum SourceType {
        BOOK, JOURNAL, REPORT, DATA, CONFERENCE, THESIS, WEB
    }
}