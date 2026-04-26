package com.airtel.inventory.service;

import com.airtel.inventory.dto.UserDTO;
import com.airtel.inventory.model.User;
import com.airtel.inventory.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream().map(UserDTO::fromEntity).collect(Collectors.toList());
    }

    public UserDTO getUserById(Long id) {
        return UserDTO.fromEntity(userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found: " + id)));
    }

    @Transactional
    public UserDTO createUser(User user) {
        if (userRepository.existsByEmployeeId(user.getEmployeeId()))
            throw new RuntimeException("Employee ID exists: " + user.getEmployeeId());
        if (user.getUsername() != null && userRepository.existsByUsername(user.getUsername()))
            throw new RuntimeException("Username exists: " + user.getUsername());
        if (user.getPasswordHash() != null && !user.getPasswordHash().isEmpty())
            user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        return UserDTO.fromEntity(userRepository.save(user));
    }

    @Transactional
    public UserDTO updateUser(Long id, User updatedUser) {
        User existing = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        existing.setFullName(updatedUser.getFullName());
        existing.setDepartment(updatedUser.getDepartment());
        existing.setEmail(updatedUser.getEmail());
        existing.setPhone(updatedUser.getPhone());
        existing.setPosition(updatedUser.getPosition());
        existing.setLocation(updatedUser.getLocation());
        existing.setRole(updatedUser.getRole());
        existing.setStatus(updatedUser.getStatus());
        return UserDTO.fromEntity(userRepository.save(existing));
    }

    @Transactional
    public void changePassword(Long id, String newPassword) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public List<UserDTO> searchUsers(String keyword) {
        return userRepository.searchByKeyword(keyword).stream().map(UserDTO::fromEntity).collect(Collectors.toList());
    }

    public List<UserDTO> getUsersByDepartment(String department) {
        return userRepository.findByDepartment(department).stream().map(UserDTO::fromEntity).collect(Collectors.toList());
    }

    public List<String> getAllDepartments() {
        return userRepository.findAllDepartments();
    }

    public UserDTO authenticate(String username, String password) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("Invalid username or password"));
        if (!passwordEncoder.matches(password, user.getPasswordHash()))
            throw new RuntimeException("Invalid username or password");
        if (user.getStatus() != User.UserStatus.ACTIVE)
            throw new RuntimeException("Account is not active. Contact admin.");
        return UserDTO.fromEntity(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        if (user.getAssignedDevices() != null && !user.getAssignedDevices().isEmpty())
            throw new RuntimeException("Cannot delete user with assigned devices.");
        userRepository.delete(user);
    }
}