package com.jacob.flowtrack.expense.comments;

import com.jacob.flowtrack.common.ApiResponse;
import com.jacob.flowtrack.member.User;
import com.jacob.flowtrack.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/expenses/{expenseId}/comments")
@RequiredArgsConstructor
public class ExpenseCommentController {

    private final ExpenseCommentService commentService;

    @PostMapping
    public ApiResponse<ExpenseCommentResponse> addComment(@PathVariable Long expenseId, @RequestBody ExpenseCommentRequest commentRequest, @AuthenticationPrincipal CustomUserDetails details) {

        User user = details.getUser();
        ExpenseCommentResponse response = commentService.addComment(expenseId, commentRequest, user);

        return ApiResponse.success(response);
    }

    @GetMapping
    public ApiResponse<List<ExpenseCommentResponse>> getComments(@PathVariable Long expenseId, @AuthenticationPrincipal CustomUserDetails details) {
        User user = details.getUser();
        return ApiResponse.success(commentService.getComments(expenseId, user));
    }

}