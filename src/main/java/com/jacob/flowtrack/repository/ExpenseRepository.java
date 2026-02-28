package com.jacob.flowtrack.repository;

import com.jacob.flowtrack.entity.Expense;
import com.jacob.flowtrack.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    Page<Expense> findByUser(User user, Pageable pageable);

    Page<Expense> findByUserAndCategory(User user, String category, Pageable pageable);

    Page<Expense> findByUserAndAmountBetween(User user, BigDecimal min, BigDecimal max, Pageable pageable);

    Page<Expense> findByUserAndCategoryAndAmountBetween(User user, String category, BigDecimal min, BigDecimal max, Pageable pageable);

    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e WHERE e.user = :user")
    BigDecimal getTotalSpentByUser(@Param("user") User user);

    @Query("SELECT e FROM Expense e WHERE e.user = :user AND (:category IS NULL OR LOWER(e.category) = :category) AND (:min IS NULL OR e.amount >= :min) AND (:max IS NULL OR e.amount <= :max)")
    Page<Expense> filterExpenses(@Param("user") User user, @Param("category") String category, @Param("min") BigDecimal min, @Param("max") BigDecimal max, Pageable pageable);

    @Query("SELECT e.category AS category, SUM(e.amount) AS total FROM Expense e WHERE e.user.id = :userId GROUP BY e.category")
    List<CategorySummary> getCategorySummary(@Param("userId") Long userId);

    @Query("SELECT EXTRACT(YEAR FROM e.createdAt) AS year, EXTRACT(MONTH FROM e.createdAt) AS month, SUM(e.amount) AS total FROM Expense e WHERE e.user.id = :userId GROUP BY EXTRACT(YEAR FROM e.createdAt), EXTRACT(MONTH FROM e.createdAt) ORDER BY EXTRACT(YEAR FROM e.createdAt), EXTRACT(MONTH FROM e.createdAt)")
    List<MonthlySummary> getMonthlySummary(@Param("userId") Long userId);

    long countByUser(User user);

}