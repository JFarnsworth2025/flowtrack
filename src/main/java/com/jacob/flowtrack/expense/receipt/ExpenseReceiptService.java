package com.jacob.flowtrack.expense.receipt;

import com.jacob.flowtrack.expense.Expense;
import com.jacob.flowtrack.expense.repository.ExpenseRepository;
import com.jacob.flowtrack.expense.service.ExpenseAuthorizationService;
import com.jacob.flowtrack.member.User;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpenseReceiptService {

    private final ExpenseReceiptRepository receiptRepository;
    private final FileStorageService storageService;
    private final ExpenseAuthorizationService authorizationService;
    private final ExpenseRepository expenseRepository;

    public ExpenseReceipt uploadReceipt(Expense expense, MultipartFile file) {
        String path = storageService.storeFile(file);
        ExpenseReceipt receipt = ExpenseReceipt.builder().expense(expense).filename(file.getOriginalFilename()).fileType(file.getContentType()).fileSize(file.getSize()).filePath(path).uploadedAt(LocalDateTime.now()).build();
        return receiptRepository.save(receipt);
    }

    public ReceiptDownloadResponse downloadReceipt(Long receiptId, User user) {
        ExpenseReceipt receipt = receiptRepository.findById(receiptId).orElseThrow(() -> new RuntimeException("Receipt not found"));
        Expense expense = receipt.getExpense();
        authorizationService.checkExpenseAccess(expense, user);
        Resource resource = storageService.loadFile(receipt.getFilePath());
        return new ReceiptDownloadResponse(receipt.getFilename(), receipt.getFileType(), resource);
    }

    public List<ReceiptResponse> getReceiptsForExpense(Long expenseId, User user) {
        Expense expense = expenseRepository.findById(expenseId).orElseThrow(() -> new RuntimeException("Expense not found"));
        authorizationService.checkExpenseAccess(expense, user);
        List<ExpenseReceipt> receipts = receiptRepository.findByExpenseId(expenseId);
        return receipts.stream().map(r -> new ReceiptResponse(r.getId(), r.getFilename(), r.getFileType(), r.getFileSize())).toList();
    }

}