package io.madeinbrain.repository;

import io.madeinbrain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    List<User> findByIsExpertTrue();

    @Query("SELECT u FROM User u WHERE u.reputation >= :minReputation ORDER BY u.reputation DESC")
    List<User> findByMinimumReputation(@Param("minReputation") Double minReputation);

    List<User> findTop10ByOrderByReputationDesc();
}