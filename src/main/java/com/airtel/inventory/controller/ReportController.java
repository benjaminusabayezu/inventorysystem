package com.airtel.inventory.controller;

import com.airtel.inventory.dto.DeviceDTO;
import com.airtel.inventory.dto.TransactionDTO;
import com.airtel.inventory.dto.UserDTO;
import com.airtel.inventory.service.DeviceService;
import com.airtel.inventory.service.TransactionService;
import com.airtel.inventory.service.UserService;
import com.airtel.inventory.util.ReportGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "*")
public class ReportController {

    private final DeviceService deviceService;
    private final UserService userService;
    private final TransactionService transactionService;
    private final ReportGenerator reportGenerator;

    @Autowired
    public ReportController(DeviceService deviceService,
                            UserService userService,
                            TransactionService transactionService,
                            ReportGenerator reportGenerator) {
        this.deviceService = deviceService;
        this.userService = userService;
        this.transactionService = transactionService;
        this.reportGenerator = reportGenerator;
    }

    @GetMapping("/devices/pdf")
    public ResponseEntity<byte[]> devicesPDF() throws Exception {
        List<DeviceDTO> devices = deviceService.getAllDevices();
        byte[] pdf = reportGenerator.generateDevicesPDF(devices);
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + reportGenerator.getReportFilename("devices", "pdf") + "\"")
            .contentType(MediaType.APPLICATION_PDF)
            .body(pdf);
    }

    @GetMapping("/devices/excel")
    public ResponseEntity<byte[]> devicesExcel() throws IOException {
        List<DeviceDTO> devices = deviceService.getAllDevices();
        byte[] excel = reportGenerator.generateDevicesExcel(devices);
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + reportGenerator.getReportFilename("devices", "xlsx") + "\"")
            .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
            .body(excel);
    }

    @GetMapping("/transactions/pdf")
    public ResponseEntity<byte[]> transactionsPDF() throws Exception {
        List<TransactionDTO> txns = transactionService.getAllTransactions();
        byte[] pdf = reportGenerator.generateTransactionsPDF(txns);
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + reportGenerator.getReportFilename("transactions", "pdf") + "\"")
            .contentType(MediaType.APPLICATION_PDF)
            .body(pdf);
    }

    @GetMapping("/transactions/excel")
    public ResponseEntity<byte[]> transactionsExcel() throws IOException {
        List<TransactionDTO> txns = transactionService.getAllTransactions();
        byte[] excel = reportGenerator.generateTransactionsExcel(txns);
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + reportGenerator.getReportFilename("transactions", "xlsx") + "\"")
            .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
            .body(excel);
    }

    @GetMapping("/users/pdf")
    public ResponseEntity<byte[]> usersPDF() throws Exception {
        List<UserDTO> users = userService.getAllUsers();
        byte[] pdf = reportGenerator.generateUsersPDF(users);
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + reportGenerator.getReportFilename("users", "pdf") + "\"")
            .contentType(MediaType.APPLICATION_PDF)
            .body(pdf);
        }
}