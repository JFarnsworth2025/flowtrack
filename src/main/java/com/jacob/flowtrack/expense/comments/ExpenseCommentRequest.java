package com.jacob.flowtrack.expense.comments;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ExpenseCommentRequest {

    @NotBlank
    private String comment;
    private Long parentCommentId;

}