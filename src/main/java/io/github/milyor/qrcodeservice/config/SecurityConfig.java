package io.github.milyor.qrcodeservice.config;


import io.github.milyor.qrcodeservice.entity.Users;
import io.github.milyor.qrcodeservice.repo.UserRepo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Set;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/**").authenticated()
                        .requestMatchers("/h2-console/**").permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .httpBasic(Customizer.withDefaults())
                .headers(httpSecurityHeadersConfigurer -> {
                    httpSecurityHeadersConfigurer.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable);
                });
        return http.build();
    }


    @Bean
    CommandLineRunner initDatabase(UserRepo userRepo, PasswordEncoder passwordEncoder) {
        return args -> {
            String username ="user";
            if (userRepo.findByUsername(username) != null) {
                String encodedPassword = passwordEncoder.encode("password");
                Set<String> authorities = Set.of("ROLE_USER", "ROLE_ADMIN");

                Users newUser = new Users(username, encodedPassword, authorities);
                userRepo.save(newUser);
                System.out.println(">>> Created default user: " + username);
            } else {
                System.out.println(">>> Default " + username + " already exists");
            }
        };
    }

}
