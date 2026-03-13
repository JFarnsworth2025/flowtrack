package com.jacob.flowtrack.expense.receipt;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class LocalFileStorageService implements FileStorageService {

    private final Path storagePath = Paths.get("uploads/receipts").toAbsolutePath().normalize();

    public LocalFileStorageService() throws IOException {
        Files.createDirectories(storagePath);
    }

    @Override
    public String storeFile(MultipartFile file) {
        try {
            String originalFileName = file.getOriginalFilename();
            if(originalFileName == null || originalFileName.isBlank()) {
                throw new RuntimeException("Invalid file name");
            }
            originalFileName = StringUtils.cleanPath(originalFileName);
            if(originalFileName.contains("..")) {
                throw new RuntimeException("Invalid file path");
            }
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path target = storagePath.resolve(fileName);
            if(!target.startsWith(storagePath)) {
                throw new RuntimeException("Invalid file path");
            }
            Files.copy(file.getInputStream(), target);
            return target.toString();
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }
    }

    @Override
    public Resource loadFile(String path) {
        try {
            Path file = Paths.get(path);
            return new UrlResource(file.toUri());
        } catch (MalformedURLException e) {
            throw new RuntimeException("Could not load file");
        }
    }

}