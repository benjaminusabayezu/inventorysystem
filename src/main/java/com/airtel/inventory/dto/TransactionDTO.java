package com.airtel.inventory.dto;

import com.airtel.inventory.model.Transaction;
import java.time.LocalDateTime;

public class TransactionDTO {
    private Long id;
    private Long deviceId;
    private String deviceName;
    private String deviceSerial;
    private Long userId;
    private String userName;
    private Long performedById;
    private String performedByName;
    private Transaction.ActionType action;
    private LocalDateTime actionDate;
    private LocalDateTime expectedReturnDate;
    private LocalDateTime actualReturnDate;
    private String notes;
    private String fromLocation;
    private String toLocation;
    private LocalDateTime createdAt;

    public static TransactionDTO fromEntity(Transaction t) {
        TransactionDTO dto = new TransactionDTO();
        dto.setId(t.getId());
        dto.setAction(t.getAction());
        dto.setActionDate(t.getActionDate());
        dto.setExpectedReturnDate(t.getExpectedReturnDate());
        dto.setActualReturnDate(t.getActualReturnDate());
        dto.setNotes(t.getNotes());
        dto.setFromLocation(t.getFromLocation());
        dto.setToLocation(t.getToLocation());
        dto.setCreatedAt(t.getCreatedAt());
        if (t.getDevice() != null) {
            dto.setDeviceId(t.getDevice().getId());
            dto.setDeviceName(t.getDevice().getName());
            dto.setDeviceSerial(t.getDevice().getSerialNumber());
        }
        if (t.getUser() != null) {
            dto.setUserId(t.getUser().getId());
            dto.setUserName(t.getUser().getFullName());
        }
        if (t.getPerformedBy() != null) {
            dto.setPerformedById(t.getPerformedBy().getId());
            dto.setPerformedByName(t.getPerformedBy().getFullName());
        }
        return dto;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getDeviceId() { return deviceId; }
    public void setDeviceId(Long deviceId) { this.deviceId = deviceId; }
    public String getDeviceName() { return deviceName; }
    public void setDeviceName(String deviceName) { this.deviceName = deviceName; }
    public String getDeviceSerial() { return deviceSerial; }
    public void setDeviceSerial(String deviceSerial) { this.deviceSerial = deviceSerial; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public Long getPerformedById() { return performedById; }
    public void setPerformedById(Long performedById) { this.performedById = performedById; }
    public String getPerformedByName() { return performedByName; }
    public void setPerformedByName(String performedByName) { this.performedByName = performedByName; }
    public Transaction.ActionType getAction() { return action; }
    public void setAction(Transaction.ActionType action) { this.action = action; }
    public LocalDateTime getActionDate() { return actionDate; }
    public void setActionDate(LocalDateTime actionDate) { this.actionDate = actionDate; }
    public LocalDateTime getExpectedReturnDate() { return expectedReturnDate; }
    public void setExpectedReturnDate(LocalDateTime expectedReturnDate) { this.expectedReturnDate = expectedReturnDate; }
    public LocalDateTime getActualReturnDate() { return actualReturnDate; }
    public void setActualReturnDate(LocalDateTime actualReturnDate) { this.actualReturnDate = actualReturnDate; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public String getFromLocation() { return fromLocation; }
    public void setFromLocation(String fromLocation) { this.fromLocation = fromLocation; }
    public String getToLocation() { return toLocation; }
    public void setToLocation(String toLocation) { this.toLocation = toLocation; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}