package io.madeinbrain.service;

import io.madeinbrain.domain.Theory;
import io.madeinbrain.domain.WeightedVote;
import io.madeinbrain.repository.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AnomalyDetectionService {

    private final VoteRepository voteRepository;

    public boolean detectBrigading(Theory theory) {
        List<WeightedVote> votes = voteRepository.findByTheory(theory);
        if (votes.size() < 5) return false;
        double avg = votes.stream().mapToDouble(WeightedVote::getRawScore).average().orElse(0);
        long extremes = votes.stream().filter(v -> Math.abs(v.getRawScore() - avg) > 0.8).count();
        return extremes > votes.size() * 0.6; // naive rule: too many extreme votes
    }
}
