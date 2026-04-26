package com.airtel.inventory.service;

import com.airtel.inventory.dto.TransactionDTO;
import com.airtel.inventory.model.Transaction;
import com.airtel.inventory.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public List<TransactionDTO> getAllTransactions() {
        return transactionRepository.findAllOrderByDateDesc().stream().map(TransactionDTO::fromEntity).collect(Collectors.toList());
    }

    public List<TransactionDTO> getTransactionsByDevice(Long deviceId) {
        return transactionRepository.findByDeviceIdOrderByDateDesc(deviceId).stream().map(TransactionDTO::fromEntity).collect(Collectors.toList());
    }

    public List<TransactionDTO> getTransactionsByUser(Long userId) {
        return transactionRepository.findByUserIdOrderByDateDesc(userId).stream().map(TransactionDTO::fromEntity).collect(Collectors.toList());
    }

    public List<TransactionDTO> getTransactionsByDateRange(LocalDateTime start, LocalDateTime end) {
        return transactionRepository.findByActionDateBetween(start, end).stream().map(TransactionDTO::fromEntity).collect(Collectors.toList());
    }

    public List<TransactionDTO> getOverdueTransactions() {
        return transactionRepository.findOverdueTransactions(LocalDateTime.now()).stream().map(TransactionDTO::fromEntity).collect(Collectors.toList());
    }

    public TransactionDTO getTransactionById(Long id) {
        Transaction t = transactionRepository.findById(id).orElseThrow(() -> new RuntimeException("Transaction not found: " + id));
        return TransactionDTO.fromEntity(t);
    }
}