package com.jacob.flowtrack.expense.comments;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class ExpenseCommentResponse {

    private String user;
    private String comment;
    private LocalDateTime createdAt;

}