package com.airtel.inventory.dto;

import com.airtel.inventory.model.Device;
import com.airtel.inventory.model.User;
import java.time.LocalDateTime;

public class UserDTO {
    private Long id;
    private String fullName;
    private String employeeId;
    private String department;
    private String email;
    private String phone;
    private String position;
    private String location;
    private User.UserRole role;
    private User.UserStatus status;
    private String username;
    private int assignedDeviceCount;
    private LocalDateTime createdAt;

    public static UserDTO fromEntity(User u) {
        UserDTO dto = new UserDTO();
        dto.setId(u.getId());
        dto.setFullName(u.getFullName());
        dto.setEmployeeId(u.getEmployeeId());
        dto.setDepartment(u.getDepartment());
        dto.setEmail(u.getEmail());
        dto.setPhone(u.getPhone());
        dto.setPosition(u.getPosition());
        dto.setLocation(u.getLocation());
        dto.setRole(u.getRole());
        dto.setStatus(u.getStatus());
        dto.setUsername(u.getUsername());
        dto.setCreatedAt(u.getCreatedAt());
        if (u.getAssignedDevices() != null) {
            dto.setAssignedDeviceCount((int) u.getAssignedDevices().stream()
                .filter(d -> d.getStatus() == Device.DeviceStatus.ASSIGNED).count());
        }
        return dto;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public User.UserRole getRole() { return role; }
    public void setRole(User.UserRole role) { this.role = role; }
    public User.UserStatus getStatus() { return status; }
    public void setStatus(User.UserStatus status) { this.status = status; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public int getAssignedDeviceCount() { return assignedDeviceCount; }
    public void setAssignedDeviceCount(int assignedDeviceCount) { this.assignedDeviceCount = assignedDeviceCount; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}