package com.jacob.flowtrack.expense.receipt;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpenseReceiptRepository extends JpaRepository<ExpenseReceipt, Long> {

    List<ExpenseReceipt> findByExpenseId(Long expenseId);

}