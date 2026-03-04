package com.jacob.flowtrack.dto;

import com.jacob.flowtrack.entity.ExpenseStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class ExpenseResponse {

    private Long id;
    private String description;
    private BigDecimal amount;
    private String category;
    private LocalDateTime createdAt;
    private ExpenseStatus status;
    private String submittedBy;
    private String approvedBy;

}