package com.airtel.inventory.controller;

import com.airtel.inventory.dto.TransactionDTO;
import com.airtel.inventory.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@CrossOrigin(origins = "*")
public class TransactionController {

    private final TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping
    public ResponseEntity<List<TransactionDTO>> getAllTransactions() {
        return ResponseEntity.ok(transactionService.getAllTransactions());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionDTO> getTransaction(@PathVariable Long id) {
        return ResponseEntity.ok(transactionService.getTransactionById(id));
    }

    @GetMapping("/device/{deviceId}")
    public ResponseEntity<List<TransactionDTO>> getByDevice(@PathVariable Long deviceId) {
        return ResponseEntity.ok(transactionService.getTransactionsByDevice(deviceId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TransactionDTO>> getByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(transactionService.getTransactionsByUser(userId));
    }

    @GetMapping("/overdue")
    public ResponseEntity<List<TransactionDTO>> getOverdue() {
        return ResponseEntity.ok(transactionService.getOverdueTransactions());
    }

    @GetMapping("/range")
    public ResponseEntity<List<TransactionDTO>> getByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return ResponseEntity.ok(transactionService.getTransactionsByDateRange(start, end));
    }
}