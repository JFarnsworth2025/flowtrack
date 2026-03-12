package com.jacob.flowtrack.expense.repository;

import com.jacob.flowtrack.expense.Expense;
import com.jacob.flowtrack.expense.comments.ExpenseComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpenseCommentRepository extends JpaRepository<ExpenseComment, Long> {

    List<ExpenseComment> findByExpenseOrderByCreatedAtDesc(Expense expense);
    List<ExpenseComment> findByExpenseAndParentIsNullOrderByCreatedAtAsc(Expense expense);

}