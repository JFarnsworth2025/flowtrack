package com.jacob.flowtrack.expense.receipt;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReceiptResponse {

    private Long id;
    private String fileName;
    private String fileType;
    private Long fileSize;

}