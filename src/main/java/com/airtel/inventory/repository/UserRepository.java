package com.airtel.inventory.repository;

import com.airtel.inventory.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmployeeId(String employeeId);

    Optional<User> findByEmail(String email);

    List<User> findByDepartment(String department);

    List<User> findByStatus(User.UserStatus status);

    List<User> findByRole(User.UserRole role);

    @Query("SELECT u FROM User u WHERE " +
           "LOWER(u.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(u.employeeId) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(u.department) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<User> searchByKeyword(@Param("keyword") String keyword);

    @Query("SELECT DISTINCT u.department FROM User u ORDER BY u.department")
    List<String> findAllDepartments();

    boolean existsByUsername(String username);

    boolean existsByEmployeeId(String employeeId);

    boolean existsByEmail(String email);
}