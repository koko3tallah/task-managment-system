package com.kerolos.tms.banquemisr.challenge05.configuration;

import com.kerolos.tms.banquemisr.challenge05.data.entity.User;
import com.kerolos.tms.banquemisr.challenge05.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@Configuration
@EnableJpaAuditing
@EnableJpaRepositories(basePackages = "com.kerolos.tms.banquemisr.challenge05.repository")
@RequiredArgsConstructor
public class JpaAppConfig {

    private final UserRepository userRepository;

    @Bean
    public AuditorAware<User> auditor() {
        return () -> {
            try {
                Authentication authentication = SecurityContextHolder.getContext()
                        .getAuthentication();

                User user = userRepository.findByEmail(authentication.getName()).orElse(null);
                return Optional.ofNullable(user);
            } catch (Exception e) {
                return Optional.empty();
            }
        };
    }

}
