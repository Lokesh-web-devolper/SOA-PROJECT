package com.parkgrid.auth.config;

import com.parkgrid.auth.entity.Role;
import com.parkgrid.auth.entity.User;
import com.parkgrid.auth.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (!userRepository.existsByEmail("admin@parkgrid.com")) {
            User admin = User.builder()
                    .fullName("ParkGrid Administrator")
                    .email("admin@parkgrid.com")
                    .phone("+1 555-0199")
                    .password(passwordEncoder.encode("Admin@123"))
                    .role(Role.ROLE_ADMIN)
                    .build();
            userRepository.save(admin);
            log.info("Initialized default ADMIN account: admin@parkgrid.com");
        }

        if (!userRepository.existsByEmail("customer@parkgrid.com")) {
            User customer = User.builder()
                    .fullName("Alex Rivera")
                    .email("customer@parkgrid.com")
                    .phone("+1 555-0144")
                    .password(passwordEncoder.encode("Customer@123"))
                    .role(Role.ROLE_CUSTOMER)
                    .build();
            userRepository.save(customer);
            log.info("Initialized default CUSTOMER account: customer@parkgrid.com");
        }

        if (!userRepository.existsByEmail("jane@parkgrid.com")) {
            User customer2 = User.builder()
                    .fullName("Jane Cooper")
                    .email("jane@parkgrid.com")
                    .phone("+1 555-0177")
                    .password(passwordEncoder.encode("Customer@123"))
                    .role(Role.ROLE_CUSTOMER)
                    .build();
            userRepository.save(customer2);
            log.info("Initialized second CUSTOMER account: jane@parkgrid.com");
        }
    }
}
