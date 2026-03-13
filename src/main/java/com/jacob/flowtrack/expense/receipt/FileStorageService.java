package com.jacob.flowtrack.expense.receipt;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {

    String storeFile(MultipartFile file);
    Resource loadFile(String path);

}