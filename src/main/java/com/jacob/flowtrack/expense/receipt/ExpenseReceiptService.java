package com.jacob.flowtrack.expense.receipt;

import com.jacob.flowtrack.expense.Expense;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ExpenseReceiptService {

    private final ExpenseReceiptRepository receiptRepository;
    private final FileStorageService storageService;

    public ExpenseReceipt uploadReceipt(Expense expense, MultipartFile file) {
        String path = storageService.storeFile(file);
        ExpenseReceipt receipt = ExpenseReceipt.builder().expense(expense).filename(file.getOriginalFilename()).fileType(file.getContentType()).fileSize(file.getSize()).filePath(path).uploadedAt(LocalDateTime.now()).build();
        return receiptRepository.save(receipt);
    }

    public ReceiptDownloadResponse downloadReceipt(Long receiptId) {
        ExpenseReceipt receipt = receiptRepository.findById(receiptId).orElseThrow(() -> new RuntimeException("Receipt not found"));
        Resource resource = storageService.loadFile(receipt.getFilePath());
        return new ReceiptDownloadResponse(receipt.getFilename(), receipt.getFileType(), resource);
    }

}