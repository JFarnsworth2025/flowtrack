package com.jacob.flowtrack.expense.repository;

import com.jacob.flowtrack.expense.Expense;
import com.jacob.flowtrack.expense.ExpenseActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ExpenseActivityRepository extends JpaRepository<ExpenseActivity, Long> {

    @Query("SELECT a FROM ExpenseActivity a JOIN FETCH a.user WHERE a.expense = :expense ORDER BY a.createdAt ASC")
    List<ExpenseActivity> findByExpenseOrderByCreatedAtAsc(@Param("expense") Expense expense);

}