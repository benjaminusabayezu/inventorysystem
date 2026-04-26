package com.airtel.inventory.config;

import com.airtel.inventory.model.Device;
import com.airtel.inventory.model.User;
import com.airtel.inventory.repository.DeviceRepository;
import com.airtel.inventory.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);
    private static final String SEED_FLAG_KEY = "data_seeded";

    private final UserRepository userRepository;
    private final DeviceRepository deviceRepository;
    private final PasswordEncoder passwordEncoder;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DataInitializer(UserRepository userRepository,
                           DeviceRepository deviceRepository,
                           PasswordEncoder passwordEncoder,
                           JdbcTemplate jdbcTemplate) {
        this.userRepository = userRepository;
        this.deviceRepository = deviceRepository;
        this.passwordEncoder = passwordEncoder;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) {
        ensureFlagTableExists();

        if (isAlreadySeeded()) {
            log.info("Database already initialized — skipping seed.");
            return;
        }

        log.info("First run detected. Seeding initial data...");
        seedUsers();
        seedDevices();
        markAsSeeded();
        log.info("Database seeded successfully. Will not re-seed on future runs.");
    }

    // ----------------------------------------------------------------
    // Flag table — survives app restarts, only cleared if DB file deleted
    // ----------------------------------------------------------------

    private void ensureFlagTableExists() {
        jdbcTemplate.execute(
            "CREATE TABLE IF NOT EXISTS app_config (" +
            "  config_key   VARCHAR(100) PRIMARY KEY, " +
            "  config_value VARCHAR(255) NOT NULL, " +
            "  created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
            ")"
        );
    }

    private boolean isAlreadySeeded() {
        try {
            Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM app_config WHERE config_key = ?",
                Integer.class, SEED_FLAG_KEY
            );
            return count != null && count > 0;
        } catch (Exception e) {
            return false;
        }
    }

    private void markAsSeeded() {
        jdbcTemplate.update(
            "INSERT INTO app_config (config_key, config_value) VALUES (?, ?)",
            SEED_FLAG_KEY, "true"
        );
    }

    // ----------------------------------------------------------------
    // Seed data — only runs ONCE ever
    // ----------------------------------------------------------------

    private void seedUsers() {
        if (!userRepository.existsByEmail("admin@airtel.rw")) {
            User admin = new User();
            admin.setFullName("System Administrator");
            admin.setEmployeeId("EMP-0001");
            admin.setDepartment("IT");
            admin.setEmail("admin@airtel.rw");
            admin.setPhone("+250788000001");
            admin.setPosition("System Administrator");
            admin.setRole(User.UserRole.ADMIN);
            admin.setStatus(User.UserStatus.ACTIVE);
            admin.setUsername("admin");
            admin.setPasswordHash(passwordEncoder.encode("admin123"));
            userRepository.save(admin);
        }

        if (!userRepository.existsByEmail("jp.habimana@airtel.rw")) {
            User itStaff = new User();
            // same setup...
            userRepository.save(itStaff);
        }

        if (!userRepository.existsByEmail("amina.uwase@airtel.rw")) {
            User staff = new User();
            // same setup...
            userRepository.save(staff);
        }

        log.info("Users after seed: {}", userRepository.count());
    }

    private void seedDevices() {

        if (!deviceRepository.existsBySerialNumber("DL-2024-001")) {
            Device laptop1 = new Device();
            laptop1.setName("Dell Latitude 5520");
            laptop1.setSerialNumber("DL-2024-001");
            // باقي fields...
            deviceRepository.save(laptop1);
        }

        if (!deviceRepository.existsBySerialNumber("HP-2024-002")) {
            Device laptop2 = new Device();
            laptop2.setSerialNumber("HP-2024-002");
            deviceRepository.save(laptop2);
        }

        if (!deviceRepository.existsBySerialNumber("SG-2024-003")) {
            Device phone = new Device();
            phone.setSerialNumber("SG-2024-003");
            deviceRepository.save(phone);
        }

        if (!deviceRepository.existsBySerialNumber("CR-2024-004")) {
            Device router = new Device();
            router.setSerialNumber("CR-2024-004");
            deviceRepository.save(router);
        }

        log.info("Devices after seed: {}", deviceRepository.count());
    }
}