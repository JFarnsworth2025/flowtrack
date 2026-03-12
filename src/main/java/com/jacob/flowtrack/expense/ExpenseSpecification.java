package com.jacob.flowtrack.expense;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ExpenseSpecification {

    public static Specification<Expense> hasWorkspace(Long workspaceId) {
        return (root, query, cb) -> cb.equal(root.get("workspace").get("id"), workspaceId);
    }
    public static Specification<Expense> hasCategory(String category) {
        return (root, query, cb) -> category == null ? null : cb.equal(cb.lower(root.get("category")), category.toLowerCase());
    }
    public static Specification<Expense> minAmount(BigDecimal min) {
        return (root, query, cb) -> min == null ? null : cb.greaterThanOrEqualTo(root.get("min"), min);
    }
    public static Specification<Expense> maxAmount(BigDecimal max) {
        return (root, query, cb) -> max == null ? null : cb.lessThanOrEqualTo(root.get("max"), max);
    }
    public static Specification<Expense> createdAfter(LocalDateTime start) {
        return (root, query, cb) -> start == null ? null : cb.greaterThanOrEqualTo(root.get("createdAt"), start);
    }
    public static Specification<Expense> createdBefore(LocalDateTime end) {
        return (root, query, cb) -> end == null ? null : cb.lessThanOrEqualTo(root.get("createdAt"), end);
    }
    public static Specification<Expense> keywordSearch(String keyword) {
        return (root, query, cb) -> keyword == null ? null : cb.like(cb.lower(root.get("description")), "%" + keyword.toLowerCase() + "%");
    }
    public static Specification<Expense> hasStatus(ExpenseStatus status) {
        return (root, query, cb) -> status == null ? null : cb.equal(root.get("status"), status);
    }
    public static Specification<Expense> byUser(String fullName) {
        return (root, query, cb) -> fullName == null ? null : cb.like(cb.lower(root.get("user").get("fullName")), "%" + fullName + "%");
    }

}