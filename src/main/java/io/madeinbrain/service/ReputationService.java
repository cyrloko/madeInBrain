// src/main/java/com/madeinbrain/service/ReputationService.java
package io.madeinbrain.service;

import io.madeinbrain.domain.UserAccount;
import org.springframework.stereotype.Service;

@Service
public class ReputationService {

    public double computeWeight(UserAccount user) {
        double base = switch (user.getRole()) {
            case ADMIN -> 1.2;
            case EXPERT -> 1.0;
            case USER -> 0.7;
        };
        double repFactor = 0.3 + Math.tanh(user.getReputation() / 100.0); // 0.3..~1.3
        return base * repFactor;
    }
}
