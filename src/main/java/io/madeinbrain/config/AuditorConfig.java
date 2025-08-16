package io.madeinbrain.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

@Configuration
public class AuditorConfig {

    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> Optional.ofNullable(SecurityUtils.getCurrentUsername()).or(() -> Optional.of("system"));
    }

    static class SecurityUtils {
        static String getCurrentUsername() {
            try {
                var ctx = org.springframework.security.core.context.SecurityContextHolder.getContext();
                if (ctx == null || ctx.getAuthentication() == null) return null;
                return ctx.getAuthentication().getName();
            } catch (Throwable t) {
                return null;
            }
        }
    }
}
