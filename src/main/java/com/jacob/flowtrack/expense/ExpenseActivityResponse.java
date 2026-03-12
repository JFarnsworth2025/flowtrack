package com.jacob.flowtrack.expense;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ExpenseActivityResponse {

    private String action;
    private String username;
    private LocalDateTime createdAt;

}