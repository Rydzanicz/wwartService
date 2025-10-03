package com.example.wwartService.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    private static final Logger logger = LoggerFactory.getLogger(FileStorageService.class);

    @Value("${file.upload.dir:uploads}")
    private String uploadDir;

    public String saveFile(final MultipartFile file, final String directory) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("Nie można zapisać pustego pliku");
        }

        final String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        if (originalFilename.contains("..")) {
            throw new IOException("Nieprawidłowa nazwa pliku: " + originalFilename);
        }

        final String contentType = file.getContentType();
        if (!isValidImageType(contentType)) {
            throw new IOException("Nieobsługiwany typ pliku: " + contentType);
        }

        final String fileExtension = getFileExtension(originalFilename);
        final String fileName = UUID.randomUUID() + "." + fileExtension;

        final Path uploadPath = Paths.get(uploadDir, directory);
        Files.createDirectories(uploadPath);

        final Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        logger.info("Zapisano plik: {}", filePath);
        return directory + "/" + fileName;
    }

    private boolean isValidImageType(String contentType) {
        return contentType != null &&
               (contentType.equals("image/jpeg") ||
                contentType.equals("image/jpg") ||
                contentType.equals("image/png") ||
                contentType.equals("image/gif"));
    }

    private String getFileExtension(String filename) {
        if (filename == null) {
            return "";
        }
        final int lastDot = filename.lastIndexOf('.');
        return lastDot > 0 ? filename.substring(lastDot + 1).toLowerCase() : "";
    }
}