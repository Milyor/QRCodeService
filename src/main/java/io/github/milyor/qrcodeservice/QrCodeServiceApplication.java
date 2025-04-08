package io.github.milyor.qrcodeservice;

import io.github.milyor.qrcodeservice.entity.Users;
import io.github.milyor.qrcodeservice.repo.UserRepo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@SpringBootApplication
@EnableCaching
public class QrCodeServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(QrCodeServiceApplication.class, args);
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
