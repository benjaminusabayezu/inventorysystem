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
        log.info("Seeding complete. Will not re-seed on future runs.");
    }

    // ── Flag table ────────────────────────────────────────

    private void ensureFlagTableExists() {
        jdbcTemplate.execute(
            "CREATE TABLE IF NOT EXISTS app_config (" +
            "  config_key   VARCHAR(100) PRIMARY KEY," +
            "  config_value VARCHAR(255) NOT NULL," +
            "  created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
            ")"
        );
    }

    private boolean isAlreadySeeded() {
        try {
            Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM app_config WHERE config_key = ?",
                Integer.class, SEED_FLAG_KEY);
            return count != null && count > 0;
        } catch (Exception e) {
            return false;
        }
    }

    private void markAsSeeded() {
        jdbcTemplate.update(
            "INSERT INTO app_config (config_key, config_value) VALUES (?, ?)",
            SEED_FLAG_KEY, "true");
    }

    // ── Users ─────────────────────────────────────────────

    private void seedUsers() {
        createUserIfNotExists(
            "System Administrator", "EMP-0001", "IT",
            "admin@airtel.rw", "+250788000001", "System Administrator",
            User.UserRole.ADMIN, "admin", "admin123"
        );
        createUserIfNotExists(
            "Jean Pierre Habimana", "EMP-0002", "IT",
            "jp.habimana@airtel.rw", "+250788000002", "IT Officer",
            User.UserRole.IT_STAFF, "jp.habimana", "staff123"
        );
        createUserIfNotExists(
            "Amina Uwase", "EMP-0003", "Finance",
            "amina.uwase@airtel.rw", "+250788000003", "Finance Officer",
            User.UserRole.STAFF, null, null
        );
        log.info("Seeded {} users", userRepository.count());
    }

    private void createUserIfNotExists(String fullName, String employeeId, String department,
                                       String email, String phone, String position,
                                       User.UserRole role, String username, String rawPassword) {
        if (userRepository.existsByEmployeeId(employeeId)) return;

        User user = new User();
        user.setFullName(fullName);
        user.setEmployeeId(employeeId);
        user.setDepartment(department);
        user.setEmail(email);
        user.setPhone(phone);
        user.setPosition(position);
        user.setRole(role);
        user.setStatus(User.UserStatus.ACTIVE);

        if (username != null && rawPassword != null) {
            user.setUsername(username);
            user.setPasswordHash(passwordEncoder.encode(rawPassword));
        }
        userRepository.save(user);
    }

    // ── Devices ───────────────────────────────────────────

    private void seedDevices() {
        createDeviceIfNotExists(
            "Dell Latitude 5520",
            Device.DeviceType.LAPTOP,       // <-- type always explicitly set
            "DL-2024-001",
            "Dell", "Latitude 5520", "Black",
            Device.DeviceCondition.GOOD,
            Device.DeviceStatus.AVAILABLE,
            LocalDateTime.of(2023, 1, 15, 0, 0),
            LocalDateTime.of(2026, 1, 15, 0, 0),
            null
        );
        createDeviceIfNotExists(
            "HP ProBook 450 G9",
            Device.DeviceType.LAPTOP,
            "HP-2024-002",
            "HP", "ProBook 450 G9", "Silver",
            Device.DeviceCondition.NEW,
            Device.DeviceStatus.AVAILABLE,
            LocalDateTime.of(2024, 3, 1, 0, 0),
            LocalDateTime.of(2027, 3, 1, 0, 0),
            null
        );
        createDeviceIfNotExists(
            "Samsung Galaxy A54 5G",
            Device.DeviceType.PHONE,
            "SG-2024-003",
            "Samsung", "Galaxy A54 5G", "Graphite",
            Device.DeviceCondition.GOOD,
            Device.DeviceStatus.AVAILABLE,
            LocalDateTime.of(2023, 6, 1, 0, 0),
            null,
            null
        );
        createDeviceIfNotExists(
            "Cisco Router RV160",
            Device.DeviceType.ROUTER,
            "CR-2024-004",
            "Cisco", "RV160", "Black",
            Device.DeviceCondition.GOOD,
            Device.DeviceStatus.UNDER_REPAIR,
            LocalDateTime.of(2022, 9, 1, 0, 0),
            null,
            "Under repair — power supply issue"
        );
        createDeviceIfNotExists(
            "Dell Monitor P2422H",
            Device.DeviceType.MONITOR,
            "DM-2024-005",
            "Dell", "P2422H", "Black",
            Device.DeviceCondition.GOOD,
            Device.DeviceStatus.AVAILABLE,
            LocalDateTime.of(2023, 3, 10, 0, 0),
            LocalDateTime.of(2026, 3, 10, 0, 0),
            null
        );
        log.info("Seeded {} devices", deviceRepository.count());
    }

    private void createDeviceIfNotExists(String name,
                                         Device.DeviceType type,
                                         String serialNumber,
                                         String brand, String model, String color,
                                         Device.DeviceCondition condition,
                                         Device.DeviceStatus status,
                                         LocalDateTime purchaseDate,
                                         LocalDateTime warrantyExpiry,
                                         String notes) {
        if (deviceRepository.existsBySerialNumber(serialNumber)) return;

        Device device = new Device();
        device.setName(name);
        device.setType(type);                   // never null
        device.setSerialNumber(serialNumber);
        device.setBrand(brand);
        device.setModel(model);
        device.setColor(color);
        device.setCondition(condition != null ? condition : Device.DeviceCondition.GOOD);
        device.setStatus(status != null ? status : Device.DeviceStatus.AVAILABLE);
        device.setPurchaseDate(purchaseDate);
        device.setWarrantyExpiry(warrantyExpiry);
        device.setNotes(notes);
        deviceRepository.save(device);
    }
}