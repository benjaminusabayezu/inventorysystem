package com.airtel.inventory.repository;

import com.airtel.inventory.model.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {

    Optional<Device> findBySerialNumber(String serialNumber);

    List<Device> findByStatus(Device.DeviceStatus status);

    List<Device> findByType(Device.DeviceType type);

    List<Device> findByCondition(Device.DeviceCondition condition);

    List<Device> findByAssignedToId(Long userId);

    @Query("SELECT d FROM Device d WHERE " +
           "LOWER(d.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(d.serialNumber) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(d.brand) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Device> searchByKeyword(@Param("keyword") String keyword);

    @Query("SELECT COUNT(d) FROM Device d WHERE d.status = :status")
    long countByStatus(@Param("status") Device.DeviceStatus status);

    @Query("SELECT d.type, COUNT(d) FROM Device d GROUP BY d.type")
    List<Object[]> countByType();

    @Query("SELECT d.status, COUNT(d) FROM Device d GROUP BY d.status")
    List<Object[]> countByStatusGrouped();

    boolean existsBySerialNumber(String serialNumber);
}