package com.jacob.flowtrack.expense.receipt;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.core.io.Resource;

@Getter@AllArgsConstructor@Setter
public class ReceiptDownloadResponse {
    private String fileName;
    private String fileType;
    private Resource resource;
}