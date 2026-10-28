package com.berkay.personalfinance.entity;

import com.berkay.personalfinance.enums.Role;
import com.berkay.personalfinance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            log.info("Creating user");
            User user1 = User.builder()
                    .email("ahmet@example.com")
                    .password("pass123") // Güvenlik aşamasında BCrypt ile hashleyeceğiz
                    .role(Role.ROLE_USER)
                    .build();
            User user2 = User.builder()
                    .email("mehmet@example.com")
                    .password("pass123")
                    .role(Role.ROLE_USER)
                    .build();
            userRepository.save(user1);
            userRepository.save(user2);
            log.info("Test kullanıcıları oluşturuldu: Ahmet (ID: 1), Mehmet (ID: 2)");
        }
    }

}



