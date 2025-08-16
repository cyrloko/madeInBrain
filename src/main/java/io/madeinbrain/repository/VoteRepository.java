package io.madeinbrain.repository;

import io.madeinbrain.domain.Theory;
import io.madeinbrain.domain.UserAccount;
import io.madeinbrain.domain.WeightedVote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VoteRepository extends JpaRepository<WeightedVote, UUID> {
    Optional<WeightedVote> findByTheoryAndVoter(Theory theory, UserAccount voter);
    List<WeightedVote> findByTheory(Theory theory);
}
