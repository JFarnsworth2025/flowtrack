package com.jacob.flowtrack.expense.comments;

import com.jacob.flowtrack.exception.ResourceNotFoundException;
import com.jacob.flowtrack.expense.Expense;
import com.jacob.flowtrack.expense.repository.ExpenseCommentRepository;
import com.jacob.flowtrack.expense.service.ExpenseAuthorizationService;
import com.jacob.flowtrack.expense.repository.ExpenseRepository;
import com.jacob.flowtrack.member.User;
import com.jacob.flowtrack.workspace.Workspace;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpenseCommentService {

    private final ExpenseRepository expenseRepository;
    private final ExpenseCommentRepository commentRepository;
    private final ExpenseAuthorizationService authorizationService;

    public ExpenseCommentResponse addComment(Long expenseId, ExpenseCommentRequest request, User user) {

        Expense expense = expenseRepository.findById(expenseId).orElseThrow(() -> new ResourceNotFoundException("Expense not found"));
        ExpenseComment parent = null;
        authorizationService.checkExpenseAccess(expense, user);

        if(request.getParentCommentId() != null) {
            parent = commentRepository.findById(request.getParentCommentId()).orElseThrow(() -> new RuntimeException("Parent comment not found"));
        }

        ExpenseComment comment = ExpenseComment.builder().expense(expense).user(user).comment(request.getComment()).createdAt(LocalDateTime.now()).parent(parent).build();
        ExpenseComment saved = commentRepository.save(comment);

        return new ExpenseCommentResponse(saved.getUser().getFullName(), saved.getComment(), saved.getCreatedAt());
    }

    public List<ExpenseCommentResponse> getComments(Long expenseId, User user) {

        Expense expense = expenseRepository.findById(expenseId).orElseThrow(() -> new ResourceNotFoundException("Expense not found"));
        authorizationService.checkExpenseAccess(expense, user);

        return commentRepository.findByExpenseOrderByCreatedAtDesc(expense).stream().map(c -> new ExpenseCommentResponse(c.getUser().getFullName(), c.getComment(), c.getCreatedAt())).toList();
    }

}