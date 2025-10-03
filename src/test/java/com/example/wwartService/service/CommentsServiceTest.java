package com.example.wwartService.service;

import com.example.wwartService.model.Comment;
import com.example.wwartService.model.CommentEntity;
import com.example.wwartService.repository.CommentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CommentsServiceTest {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private CommentRepository commentRepository;
    private CommentsService commentsService;
    private FileStorageService fileStorageService;

    @BeforeEach
    void setUp() {
        commentRepository = mock(CommentRepository.class);
        fileStorageService = mock(FileStorageService.class);

        commentsService = new CommentsService(commentRepository, fileStorageService);
    }

    @Test
    public void testGetCommentsByProductIdReturnsMappedList() {
        // given
        final String productId = "P01";
        final String createdDateStr = "2024-01-01 14:30:00";
        final LocalDateTime createdDate = LocalDateTime.parse(createdDateStr, formatter);
        final String photoPath = "resources/commentsPhoto";

        final CommentEntity entity = new CommentEntity(productId, "Jan Kowalski", "Świetny produkt!", 5, createdDateStr, photoPath);

        // when
        when(commentRepository.findCommentsByProductId(productId)).thenReturn(List.of(entity));

        final List<Comment> comments = commentsService.getCommentsByProductId(productId);

        // then
        assertNotNull(comments);
        assertEquals(1, comments.size());

        final Comment comment = comments.get(0);
        assertEquals(productId, comment.getProductId());
        assertEquals("Jan Kowalski", comment.getAuthor());
        assertEquals("Świetny produkt!", comment.getText());
        assertEquals(5, comment.getRating());
        assertEquals(createdDate, comment.getCreatedDate());

        verify(commentRepository, times(1)).findCommentsByProductId(productId);
    }

    @Test
    public void testSaveCommentCallsRepositorySave() throws IOException {
        // given
        final String productId = "P01";
        final MockMultipartFile image = new MockMultipartFile("image", "commentsPhoto.jpg", "image/jpeg", "Dummy Image Content".getBytes());
        final String savedPhotoPath = "comments/photo123.jpg";

        final CommentEntity savedEntityMock = new CommentEntity(productId, "Jan Kowalski", "Świetny produkt!", 5, "2024-01-01 14:30:00",
                                                                savedPhotoPath);
        savedEntityMock.setId(1L);

        when(fileStorageService.saveFile(image, "comments")).thenReturn(savedPhotoPath);
        when(commentRepository.save(org.mockito.ArgumentMatchers.any(CommentEntity.class))).thenReturn(savedEntityMock);

        final Comment comment = new Comment(productId, "Jan Kowalski", "Świetny produkt!", 5, "2024-01-01 14:30:00");

        // when
        commentsService.saveComment(comment, image);

        // then
        ArgumentCaptor<CommentEntity> captor = ArgumentCaptor.forClass(CommentEntity.class);
        verify(commentRepository, times(1)).save(captor.capture());

        final CommentEntity capturedEntity = captor.getValue();

        assertEquals(comment.getProductId(), capturedEntity.getProductId());
        assertEquals(comment.getAuthor(), capturedEntity.getAuthor());
        assertEquals(comment.getText(), capturedEntity.getText());
        assertEquals(comment.getRating(), capturedEntity.getRating());
        assertEquals(comment.getCreatedDate(), LocalDateTime.parse(capturedEntity.getCreatedDate(), formatter));
        assertEquals(savedPhotoPath, capturedEntity.getPhotoPath());
    }

}
