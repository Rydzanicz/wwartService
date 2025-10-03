package com.example.wwartService.service;

import com.example.wwartService.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FileStorageServiceTest {

    private FileStorageService fileStorageService;

    @BeforeEach
    void setUp() {
        fileStorageService = new FileStorageService();
        TestUtils.setField(fileStorageService, "uploadDir", "uploads_test");
    }

    @Test
    void testSaveFileThrowsIfEmpty() {
        // given
        final MockMultipartFile emptyFile = new MockMultipartFile("file", new byte[0]);
        final IOException exception = assertThrows(IOException.class, () -> {
            fileStorageService.saveFile(emptyFile, "comments");
        });

        // when
        //then
        assertEquals("Nie można zapisać pustego pliku", exception.getMessage());
    }

    @Test
    void testSaveFileThrowsIfFilenameInvalid() {
        // given
        final MockMultipartFile invalidFile = new MockMultipartFile("file", "../hack.jpg", "image/jpeg", "data".getBytes());
        final IOException exception = assertThrows(IOException.class, () -> {
            fileStorageService.saveFile(invalidFile, "comments");
        });

        // when
        //then
        assertTrue(exception.getMessage().contains("Nieprawidłowa nazwa pliku"));
    }

    @Test
    void testSaveFileThrowsIfUnsupportedContentType() {
        // given
        final MockMultipartFile txtFile = new MockMultipartFile("file", "file.txt", "text/plain", "data".getBytes());
        final IOException exception = assertThrows(IOException.class, () -> {
            fileStorageService.saveFile(txtFile, "comments");
        });

        // when
        //then
        assertTrue(exception.getMessage().contains("Nieobsługiwany typ pliku"));
    }

    @Test
    void testSaveFileSavesFileCorrectly() throws IOException {
        // given
        final byte[] fileData = "dummy image content".getBytes();
        final MockMultipartFile validFile = new MockMultipartFile("file", "photo.jpeg", "image/jpeg", fileData);

        // when
        final Path uploadPath = Paths.get("uploads_test/comments");
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        //then
        final String savedPath = fileStorageService.saveFile(validFile, "comments");
        assertNotNull(savedPath);
        assertTrue(savedPath.startsWith("comments/"));

        final Path savedFile = uploadPath.resolve(savedPath.substring("comments/".length()));
        assertTrue(Files.exists(savedFile));

        final byte[] savedContent = Files.readAllBytes(savedFile);
        assertArrayEquals(fileData, savedContent);

        // Cleanup
        Files.deleteIfExists(savedFile);
    }
}
