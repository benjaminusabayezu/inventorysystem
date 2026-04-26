package com.airtel.inventory.controller;

import com.airtel.inventory.dto.DeviceDTO;
import com.airtel.inventory.model.Device;
import com.airtel.inventory.service.DeviceService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/devices")
@CrossOrigin(origins = "*")
public class DeviceController {

    private final DeviceService deviceService;

    @Autowired
    public DeviceController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    @GetMapping
    public ResponseEntity<List<DeviceDTO>> getAllDevices() {
        return ResponseEntity.ok(deviceService.getAllDevices());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeviceDTO> getDevice(@PathVariable Long id) {
        return ResponseEntity.ok(deviceService.getDeviceById(id));
    }

    @GetMapping("/serial/{serial}")
    public ResponseEntity<DeviceDTO> getDeviceBySerial(@PathVariable String serial) {
        return ResponseEntity.ok(deviceService.getDeviceBySerial(serial));
    }

    @GetMapping("/search")
    public ResponseEntity<List<DeviceDTO>> searchDevices(@RequestParam String keyword) {
        return ResponseEntity.ok(deviceService.searchDevices(keyword));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<DeviceDTO>> getByStatus(@PathVariable Device.DeviceStatus status) {
        return ResponseEntity.ok(deviceService.getDevicesByStatus(status));
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Long>> getStats() {
        return ResponseEntity.ok(deviceService.getDashboardStats());
    }

    @PostMapping
    public ResponseEntity<DeviceDTO> createDevice(@Valid @RequestBody Device device) {
        return ResponseEntity.status(HttpStatus.CREATED).body(deviceService.createDevice(device));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DeviceDTO> updateDevice(@PathVariable Long id, @Valid @RequestBody Device device) {
        return ResponseEntity.ok(deviceService.updateDevice(id, device));
    }

    @PostMapping("/{id}/assign")
    public ResponseEntity<DeviceDTO> assignDevice(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Long userId = Long.parseLong(body.get("userId").toString());
        Long performedById = Long.parseLong(body.get("performedById").toString());
        String notes = body.getOrDefault("notes", "").toString();
        LocalDateTime expectedReturn = body.containsKey("expectedReturnDate")
            ? LocalDateTime.parse(body.get("expectedReturnDate").toString()) : null;
        return ResponseEntity.ok(deviceService.assignDevice(id, userId, performedById, expectedReturn, notes));
    }

    @PostMapping("/{id}/return")
    public ResponseEntity<DeviceDTO> returnDevice(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Long performedById = Long.parseLong(body.get("performedById").toString());
        String notes = body.getOrDefault("notes", "").toString();
        return ResponseEntity.ok(deviceService.returnDevice(id, performedById, notes));
    }

    @PostMapping("/{id}/repair")
    public ResponseEntity<DeviceDTO> sendToRepair(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Long performedById = Long.parseLong(body.get("performedById").toString());
        String notes = body.getOrDefault("notes", "").toString();
        return ResponseEntity.ok(deviceService.sendToRepair(id, performedById, notes));
    }

    @PostMapping("/{id}/lost")
    public ResponseEntity<DeviceDTO> markLost(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Long performedById = Long.parseLong(body.get("performedById").toString());
        String notes = body.getOrDefault("notes", "").toString();
        return ResponseEntity.ok(deviceService.markAsLost(id, performedById, notes));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDevice(@PathVariable Long id) {
        deviceService.deleteDevice(id);
        return ResponseEntity.noContent().build();
    }
}