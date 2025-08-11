package io.madeinbrain.service;

import io.madeinbrain.entity.User;
import io.madeinbrain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private UserRepository userRepository;

    private PasswordEncoder passwordEncoder;

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User createUser(String username, String email, String password) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        User user = User.builder().username(username).email(email).passwordHash(passwordEncoder.encode(password)).build();
        return userRepository.save(user);
    }

    public boolean validatePassword(User user, String password) {
        return passwordEncoder.matches(password, user.getPasswordHash());
    }

    public List<User> getExperts() {
        return userRepository.findByIsExpertTrue();
    }

    public List<User> getTopUsersByReputation(int limit) {
        return userRepository.findTop10ByOrderByReputationDesc()
                .stream()
                .limit(limit)
                .toList();
    }

    public void updateUserReputation(User user, double reputationChange) {
        user.setReputation(user.getReputation() + reputationChange);
        userRepository.save(user);
    }

    public User promoteToExpert(Long userId, String expertDomains) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setIsExpert(true);
        user.setExpertDomains(expertDomains);
        user.setRole(User.Role.EXPERT);

        return userRepository.save(user);
    }
}