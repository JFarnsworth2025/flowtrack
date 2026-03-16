package com.jacob.flowtrack.expense.comments;

import com.jacob.flowtrack.expense.Expense;
import com.jacob.flowtrack.member.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expense_id", nullable = false)
    private Expense expense;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 1000)
    private String comment;

    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "parent_comment_id")
    private ExpenseComment parent;

    @OneToMany(mappedBy = "parent")
    private List<ExpenseComment> replies;

}