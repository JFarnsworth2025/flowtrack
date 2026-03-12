package com.jacob.flowtrack.expense;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ExpenseSpecification {

    public static Specification<Expense> search(Long workspaceId, String keyword, String category, BigDecimal min, BigDecimal max, LocalDateTime startDate, LocalDateTime endDate) {

        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            if(workspaceId != null) { predicates.add(cb.equal(root.get("workspace").get("id"), workspaceId)); }
            if(keyword != null && !keyword.isBlank()) { predicates.add(cb.like(cb.lower(root.get("description")), "%" + keyword.toLowerCase() + "%")); }
            if(category != null && !category.isBlank()) { predicates.add(cb.equal(cb.lower(root.get("category")), category.toLowerCase())); }
            if(min != null) { predicates.add(cb.greaterThanOrEqualTo(root.get("amount"), min)); }
            if(max != null) { predicates.add(cb.lessThanOrEqualTo(root.get("amount"), max)); }
            if(startDate != null) { predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), startDate)); }
            if(endDate != null) { predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), endDate)); }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

    }

}