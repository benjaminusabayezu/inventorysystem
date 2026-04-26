package com.airtel.inventory.service;

import com.airtel.inventory.dto.DeviceDTO;
import com.airtel.inventory.model.Device;
import com.airtel.inventory.model.Transaction;
import com.airtel.inventory.model.User;
import com.airtel.inventory.repository.DeviceRepository;
import com.airtel.inventory.repository.TransactionRepository;
import com.airtel.inventory.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DeviceService {

    private final DeviceRepository deviceRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    @Autowired
    public DeviceService(DeviceRepository deviceRepository,
                         UserRepository userRepository,
                         TransactionRepository transactionRepository) {
        this.deviceRepository = deviceRepository;
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
    }

    public List<DeviceDTO> getAllDevices() {
        return deviceRepository.findAll().stream().map(DeviceDTO::fromEntity).collect(Collectors.toList());
    }

    public DeviceDTO getDeviceById(Long id) {
        Device device = deviceRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Device not found with id: " + id));
        return DeviceDTO.fromEntity(device);
    }

    public DeviceDTO getDeviceBySerial(String serial) {
        Device device = deviceRepository.findBySerialNumber(serial)
            .orElseThrow(() -> new RuntimeException("Device not found with serial: " + serial));
        return DeviceDTO.fromEntity(device);
    }

    @Transactional
    public DeviceDTO createDevice(Device device) {
        if (deviceRepository.existsBySerialNumber(device.getSerialNumber())) {
            throw new RuntimeException("Device with serial number already exists: " + device.getSerialNumber());
        }
        device.setStatus(Device.DeviceStatus.AVAILABLE);
        return DeviceDTO.fromEntity(deviceRepository.save(device));
    }

    @Transactional
    public DeviceDTO updateDevice(Long id, Device updatedDevice) {
        Device existing = deviceRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Device not found with id: " + id));
        existing.setName(updatedDevice.getName());
        existing.setType(updatedDevice.getType());
        existing.setBrand(updatedDevice.getBrand());
        existing.setModel(updatedDevice.getModel());
        existing.setColor(updatedDevice.getColor());
        existing.setCondition(updatedDevice.getCondition());
        existing.setPurchaseDate(updatedDevice.getPurchaseDate());
        existing.setWarrantyExpiry(updatedDevice.getWarrantyExpiry());
        existing.setNotes(updatedDevice.getNotes());
        return DeviceDTO.fromEntity(deviceRepository.save(existing));
    }

    @Transactional
    public DeviceDTO assignDevice(Long deviceId, Long userId, Long performedById, LocalDateTime expectedReturn, String notes) {
        Device device = deviceRepository.findById(deviceId).orElseThrow(() -> new RuntimeException("Device not found"));
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        User performedBy = userRepository.findById(performedById).orElseThrow(() -> new RuntimeException("Performer not found"));

        if (device.getStatus() != Device.DeviceStatus.AVAILABLE && device.getStatus() != Device.DeviceStatus.RESERVED) {
            throw new RuntimeException("Device is not available. Status: " + device.getStatus());
        }
        device.setStatus(Device.DeviceStatus.ASSIGNED);
        device.setAssignedTo(user);
        deviceRepository.save(device);

        Transaction t = new Transaction();
        t.setDevice(device); t.setUser(user); t.setPerformedBy(performedBy);
        t.setAction(Transaction.ActionType.ASSIGNED);
        t.setActionDate(LocalDateTime.now());
        t.setExpectedReturnDate(expectedReturn);
        t.setNotes(notes);
        transactionRepository.save(t);
        return DeviceDTO.fromEntity(device);
    }

    @Transactional
    public DeviceDTO returnDevice(Long deviceId, Long performedById, String notes) {
        Device device = deviceRepository.findById(deviceId).orElseThrow(() -> new RuntimeException("Device not found"));
        User performedBy = userRepository.findById(performedById).orElseThrow(() -> new RuntimeException("Performer not found"));
        if (device.getStatus() != Device.DeviceStatus.ASSIGNED) throw new RuntimeException("Device is not assigned.");

        User previousUser = device.getAssignedTo();
        device.setStatus(Device.DeviceStatus.AVAILABLE);
        device.setAssignedTo(null);
        deviceRepository.save(device);

        Transaction t = new Transaction();
        t.setDevice(device); t.setUser(previousUser); t.setPerformedBy(performedBy);
        t.setAction(Transaction.ActionType.RETURNED);
        t.setActionDate(LocalDateTime.now());
        t.setActualReturnDate(LocalDateTime.now());
        t.setNotes(notes);
        transactionRepository.save(t);
        return DeviceDTO.fromEntity(device);
    }

    @Transactional
    public DeviceDTO sendToRepair(Long deviceId, Long performedById, String notes) {
        Device device = deviceRepository.findById(deviceId).orElseThrow(() -> new RuntimeException("Device not found"));
        User performedBy = userRepository.findById(performedById).orElseThrow(() -> new RuntimeException("Performer not found"));

        User previousUser = device.getAssignedTo();
        device.setStatus(Device.DeviceStatus.UNDER_REPAIR);
        device.setAssignedTo(null);
        deviceRepository.save(device);

        Transaction t = new Transaction();
        t.setDevice(device); t.setUser(previousUser); t.setPerformedBy(performedBy);
        t.setAction(Transaction.ActionType.SENT_TO_REPAIR);
        t.setActionDate(LocalDateTime.now()); t.setNotes(notes);
        transactionRepository.save(t);
        return DeviceDTO.fromEntity(device);
    }

    @Transactional
    public DeviceDTO markAsLost(Long deviceId, Long performedById, String notes) {
        Device device = deviceRepository.findById(deviceId).orElseThrow(() -> new RuntimeException("Device not found"));
        User performedBy = userRepository.findById(performedById).orElseThrow(() -> new RuntimeException("Performer not found"));

        User previousUser = device.getAssignedTo();
        device.setStatus(Device.DeviceStatus.LOST);
        device.setAssignedTo(null);
        deviceRepository.save(device);

        Transaction t = new Transaction();
        t.setDevice(device); t.setUser(previousUser); t.setPerformedBy(performedBy);
        t.setAction(Transaction.ActionType.LOST);
        t.setActionDate(LocalDateTime.now()); t.setNotes(notes);
        transactionRepository.save(t);
        return DeviceDTO.fromEntity(device);
    }

    public List<DeviceDTO> searchDevices(String keyword) {
        return deviceRepository.searchByKeyword(keyword).stream().map(DeviceDTO::fromEntity).collect(Collectors.toList());
    }

    public List<DeviceDTO> getDevicesByStatus(Device.DeviceStatus status) {
        return deviceRepository.findByStatus(status).stream().map(DeviceDTO::fromEntity).collect(Collectors.toList());
    }

    public Map<String, Long> getDashboardStats() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("total", deviceRepository.count());
        stats.put("available", deviceRepository.countByStatus(Device.DeviceStatus.AVAILABLE));
        stats.put("assigned", deviceRepository.countByStatus(Device.DeviceStatus.ASSIGNED));
        stats.put("repair", deviceRepository.countByStatus(Device.DeviceStatus.UNDER_REPAIR));
        stats.put("lost", deviceRepository.countByStatus(Device.DeviceStatus.LOST));
        return stats;
    }

    @Transactional
    public void deleteDevice(Long id) {
        Device device = deviceRepository.findById(id).orElseThrow(() -> new RuntimeException("Device not found"));
        if (device.getStatus() == Device.DeviceStatus.ASSIGNED) throw new RuntimeException("Cannot delete assigned device.");
        deviceRepository.delete(device);
    }
}