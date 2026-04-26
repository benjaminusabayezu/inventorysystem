package com.airtel.inventory.repository;

import com.airtel.inventory.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByDeviceId(Long deviceId);

    List<Transaction> findByUserId(Long userId);

    List<Transaction> findByAction(Transaction.ActionType action);

    List<Transaction> findByActionDateBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT t FROM Transaction t ORDER BY t.actionDate DESC")
    List<Transaction> findAllOrderByDateDesc();

    @Query("SELECT t FROM Transaction t WHERE t.device.id = :deviceId ORDER BY t.actionDate DESC")
    List<Transaction> findByDeviceIdOrderByDateDesc(@Param("deviceId") Long deviceId);

    @Query("SELECT t FROM Transaction t WHERE t.user.id = :userId ORDER BY t.actionDate DESC")
    List<Transaction> findByUserIdOrderByDateDesc(@Param("userId") Long userId);

    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.action = :action")
    long countByAction(@Param("action") Transaction.ActionType action);

    @Query("SELECT t FROM Transaction t WHERE t.expectedReturnDate < :date AND t.actualReturnDate IS NULL AND t.action = 'ASSIGNED'")
    List<Transaction> findOverdueTransactions(@Param("date") LocalDateTime date);
}