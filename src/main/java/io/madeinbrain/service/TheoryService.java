// src/main/java/com/madeinbrain/service/TheoryService.java
package io.madeinbrain.service;

import io.madeinbrain.domain.*;
import io.madeinbrain.dto.EvidenceDtos;
import io.madeinbrain.dto.SourceDtos;
import io.madeinbrain.dto.TheoryDtos;
import io.madeinbrain.dto.VoteDtos;
import io.madeinbrain.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class TheoryService {

    private final TheoryRepository theoryRepository;
    private final SourceRepository sourceRepository;
    private final EvidenceRepository evidenceRepository;
    private final VoteRepository voteRepository;
    private final UserRepository userRepository;
    private final ValidationService validationService;
    private final ReputationService reputationService;
    private final ScoringService scoringService;
    private final RevisionService revisionService;
    private final AnomalyDetectionService anomalyDetectionService;

    public Theory create(TheoryDtos.CreateTheoryRequest req) {
        Theory t = Theory.builder()
                .title(req.getTitle())
                .description(req.getDescription())
                .domain(req.getDomain())
                .tags(Optional.ofNullable(req.getTags()).orElse(Set.of()))
                .locked(false)
                .currentConfidence(0.5)
                .createdAt(ZonedDateTime.now().toInstant())
                .createdBy("ANONYMOUS")
                .version(0)
                .build();
        Theory saved = theoryRepository.save(t);
        revisionService.record("Theory", saved.getId().toString(), actor(), "create", Map.of(
                "title", req.getTitle(), "domain", String.valueOf(req.getDomain()), "tags", req.getTags()
        ));
        scoringService.recompute(saved);
        return saved;
    }

    @Transactional
    public Theory update(UUID id, TheoryDtos.UpdateTheoryRequest req) {
        Theory t = theoryRepository.findById(id).orElseThrow();
        if (t.isLocked()) throw new IllegalStateException("theory locked");
        Map<String, Object> changes = new LinkedHashMap<>();
        if (req.getTitle() != null) { changes.put("title", req.getTitle()); t.setTitle(req.getTitle()); }
        if (req.getDescription() != null) { changes.put("description", req.getDescription()); t.setDescription(req.getDescription()); }
        if (req.getDomain() != null) { changes.put("domain", String.valueOf(req.getDomain())); t.setDomain(req.getDomain()); }
        if (req.getTags() != null) { changes.put("tags", req.getTags()); t.setTags(req.getTags()); }
        theoryRepository.save(t);
        revisionService.record("Theory", t.getId().toString(), actor(), req.getReason() == null ? "update" : req.getReason(), changes);
        scoringService.recompute(t);
        return t;
    }

    public Source createOrUpdateSource(SourceDtos.CreateSourceRequest req) {
        Source s = sourceRepository.findByUrl(req.getUrl()).orElseGet(() -> Source.builder().url(req.getUrl()).build());
        s.setTitle(req.getTitle());
        s.setType(req.getType());
        s.setPublisher(req.getPublisher());
        s.setPublishedAt(req.getPublishedAt());
        s.setPeerReviewed(req.isPeerReviewed());
        validationService.enrich(s);
        Source saved = sourceRepository.save(s);
        revisionService.record("Source", saved.getId().toString(), actor(), "upsert", Map.of("url", saved.getUrl()));
        return saved;
    }

    @Transactional
    public Evidence addEvidence(EvidenceDtos.AddEvidenceRequest req) {
        Theory theory = theoryRepository.findById(req.getTheoryId()).orElseThrow();
        Source source = sourceRepository.findById(req.getSourceId()).orElseThrow();
        Evidence ev = Evidence.builder()
                .theory(theory)
                .source(source)
                .stance(req.getStance())
                .weight(source.getIntrinsicReliability() * stanceWeight(req.getStance()))
                .notes(req.getNotes())
                .build();
        evidenceRepository.save(ev);
        revisionService.record("Evidence", ev.getId().toString(), actor(), "add", Map.of(
                "theoryId", theory.getId().toString(), "sourceId", source.getId().toString(), "stance", String.valueOf(req.getStance())
        ));
        scoringService.recompute(theory);
        return ev;
    }

    public WeightedVote castVote(VoteDtos.CastVoteRequest req) {
        Theory theory = theoryRepository.findById(req.getTheoryId()).orElseThrow();
        UserAccount voter = userRepository.findByUsername(actor()).orElseGet(() ->
                userRepository.save(UserAccount.builder().username(actor()).role(Role.USER).reputation(0).build())
        );

        double weight = reputationService.computeWeight(voter);
        double raw = clamp(req.getScore(), -1, 1);
        double weighted = raw * weight;

        WeightedVote vote = voteRepository.findByTheoryAndVoter(theory, voter).orElse(
                WeightedVote.builder().theory(theory).voter(voter).build()
        );
        vote.setRawScore(raw);
        vote.setReputationAtTime(voter.getReputation());
        vote.setWeightedScore(clamp(weighted, -1.5, 1.5));
        vote.setRationale(req.getRationale());
        voteRepository.save(vote);

        revisionService.record("Vote", vote.getId() == null ? "new" : vote.getId().toString(), actor(), "cast",
                Map.of("theoryId", theory.getId().toString(), "raw", raw, "weighted", vote.getWeightedScore()));

        if (!anomalyDetectionService.detectBrigading(theory)) {
            scoringService.recompute(theory);
        }
        return vote;
    }

    private static double stanceWeight(Stance stance) {
        return switch (stance) {
            case SUPPORTS -> 1.0;
            case REFUTES -> 1.0;
            case NEUTRAL -> 0.2;
        };
    }

    private static double clamp(double v, double min, double max) {
        return Math.max(min, Math.min(max, v));
    }

    private static String actor() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : "anonymous";
    }
}
