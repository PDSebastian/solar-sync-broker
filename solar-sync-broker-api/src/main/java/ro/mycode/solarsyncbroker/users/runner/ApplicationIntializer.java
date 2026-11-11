package ro.mycode.solarsyncbroker.users.runner;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ro.mycode.solarsyncbroker.users.model.User;
import ro.mycode.solarsyncbroker.users.model.UserType;
import ro.mycode.solarsyncbroker.users.repository.UserRepository;

@Component
public class ApplicationIntializer implements ApplicationRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${ADMIN_EMAIL:#{null}}")
    private String adminEmail;

    @Value("${ADMIN_PASSWORD:#{null}}")
    private String adminPassword;

    public ApplicationIntializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {

        if (adminEmail != null && !adminEmail.isBlank() &&
                adminPassword != null && !adminPassword.isBlank()) {

            if (userRepository.findByEmail(adminEmail).isEmpty()) {
                User admin = User.builder()
                        .firstName("Admin")
                        .lastName("System")
                        .email(adminEmail)
                        .password(passwordEncoder.encode(adminPassword))
                        .age(30)
                        .userType(UserType.ADMIN)
                        .build();

                userRepository.save(admin);
            }
        }
    }
}
