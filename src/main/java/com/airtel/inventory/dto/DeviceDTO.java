package com.airtel.inventory.dto;

import com.airtel.inventory.model.Device;
import java.time.LocalDateTime;

public class DeviceDTO {
    private Long id;
    private String name;
    private Device.DeviceType type;
    private String serialNumber;
    private Device.DeviceCondition condition;
    private Device.DeviceStatus status;
    private String brand;
    private String model;
    private String color;
    private LocalDateTime purchaseDate;
    private LocalDateTime warrantyExpiry;
    private String notes;
    private Long assignedToUserId;
    private String assignedToUserName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static DeviceDTO fromEntity(Device d) {
        DeviceDTO dto = new DeviceDTO();
        dto.setId(d.getId());
        dto.setName(d.getName());
        dto.setType(d.getType());
        dto.setSerialNumber(d.getSerialNumber());
        dto.setCondition(d.getCondition());
        dto.setStatus(d.getStatus());
        dto.setBrand(d.getBrand());
        dto.setModel(d.getModel());
        dto.setColor(d.getColor());
        dto.setPurchaseDate(d.getPurchaseDate());
        dto.setWarrantyExpiry(d.getWarrantyExpiry());
        dto.setNotes(d.getNotes());
        dto.setCreatedAt(d.getCreatedAt());
        dto.setUpdatedAt(d.getUpdatedAt());
        if (d.getAssignedTo() != null) {
            dto.setAssignedToUserId(d.getAssignedTo().getId());
            dto.setAssignedToUserName(d.getAssignedTo().getFullName());
        }
        return dto;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Device.DeviceType getType() { return type; }
    public void setType(Device.DeviceType type) { this.type = type; }
    public String getSerialNumber() { return serialNumber; }
    public void setSerialNumber(String serialNumber) { this.serialNumber = serialNumber; }
    public Device.DeviceCondition getCondition() { return condition; }
    public void setCondition(Device.DeviceCondition condition) { this.condition = condition; }
    public Device.DeviceStatus getStatus() { return status; }
    public void setStatus(Device.DeviceStatus status) { this.status = status; }
    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    public LocalDateTime getPurchaseDate() { return purchaseDate; }
    public void setPurchaseDate(LocalDateTime purchaseDate) { this.purchaseDate = purchaseDate; }
    public LocalDateTime getWarrantyExpiry() { return warrantyExpiry; }
    public void setWarrantyExpiry(LocalDateTime warrantyExpiry) { this.warrantyExpiry = warrantyExpiry; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public Long getAssignedToUserId() { return assignedToUserId; }
    public void setAssignedToUserId(Long assignedToUserId) { this.assignedToUserId = assignedToUserId; }
    public String getAssignedToUserName() { return assignedToUserName; }
    public void setAssignedToUserName(String assignedToUserName) { this.assignedToUserName = assignedToUserName; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}