package io.madeinbrain.service;

import io.madeinbrain.domain.Source;
import io.madeinbrain.domain.SourceType;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * Zero-trust-ish source validation. Real integrations with CrossRef/PubMed/ArXiv would go here.
 * For this skeleton, we heuristically score reliability by type & publisher whitelist/blacklist.
 */
@Service
public class ValidationService {

    private static final Set<String> TRUSTED_PUBLISHERS = Set.of(
            "Nature", "Science", "The Lancet", "Cell", "WHO", "OECD", "World Bank", "IPCC"
    );

    private static final Set<String> LOW_TRUST_PUBLISHERS = Set.of(
            "Predatory Journal", "Random Blog"
    );

    public double computeIntrinsicReliability(SourceType type, String publisher, boolean peerReviewed) {
        double base = switch (type) {
            case PEER_REVIEWED -> 0.85;
            case PREPRINT -> 0.55;
            case DATASET -> 0.75;
            case GOVERNMENT_REPORT -> 0.7;
            case NGO_REPORT -> 0.6;
            case NEWSROOM -> 0.5;
            case BOOK -> 0.6;
            case VIDEO -> 0.35;
            default -> 0.4;
        };
        if (peerReviewed) base += 0.1;
        if (TRUSTED_PUBLISHERS.contains(publisher)) base += 0.1;
        if (LOW_TRUST_PUBLISHERS.contains(publisher)) base -= 0.2;
        return Math.max(0.0, Math.min(1.0, base));
    }

    public void enrich(Source s) {
        s.setIntrinsicReliability(computeIntrinsicReliability(s.getType(), s.getPublisher(), s.isPeerReviewed()));
    }
}
