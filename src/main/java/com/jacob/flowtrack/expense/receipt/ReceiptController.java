package com.jacob.flowtrack.expense.receipt;

import com.jacob.flowtrack.member.User;
import com.jacob.flowtrack.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/receipts")
@RequiredArgsConstructor
public class ReceiptController {

    private final ExpenseReceiptService receiptService;
    private final ReceiptDownloadResponse downloadResponse;

    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> downloadReceipt(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long id) {
        User user = userDetails.getUser();
        ReceiptDownloadResponse response = receiptService.downloadReceipt(id, user);
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(response.getFileType())).header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + response.getFileName() + "\"").body(response.getResource());
    }

}