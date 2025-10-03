package com.example.wwartService.service;

import com.example.wwartService.model.Comment;
import com.example.wwartService.model.CommentEntity;
import com.example.wwartService.repository.CommentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CommentsServiceTest {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private CommentRepository commentRepository;
    private CommentsService commentsService;

    @BeforeEach
    void setUp() {
        commentRepository = mock(CommentRepository.class);
        commentsService = new CommentsService(commentRepository);
    }

    @Test
    public void testGetCommentsByProductIdReturnsMappedList() {
        // given
        final String productId = "P01";
        final String createdDateStr = "2024-01-01 14:30:00";
        final LocalDateTime createdDate = LocalDateTime.parse(createdDateStr, formatter);
        final CommentEntity entity = new CommentEntity(
                productId,
                "Jan Kowalski",
                "Świetny produkt!",
                5,
                createdDateStr
        );

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
    public void testSaveCommentCallsRepositorySave() {
        // given
        final String productId = "P01";
        final LocalDateTime createdDate = LocalDateTime.parse("2024-01-01 14:30:00", formatter);
        final Comment comment = new Comment(productId, "Jan Kowalski", "Świetny produkt!", 5, createdDate);

        // when
        commentsService.saveComment(comment);

        // then
        ArgumentCaptor<CommentEntity> captor = ArgumentCaptor.forClass(CommentEntity.class);
        verify(commentRepository, times(1)).save(captor.capture());

        final CommentEntity savedEntity = captor.getValue();
        assertEquals(comment.getProductId(), savedEntity.getProductId());
        assertEquals(comment.getAuthor(), savedEntity.getAuthor());
        assertEquals(comment.getText(), savedEntity.getText());
        assertEquals(comment.getRating(), savedEntity.getRating());
        assertEquals(comment.getCreatedDate(), LocalDateTime.parse(savedEntity.getCreatedDate(), formatter));
    }
}
