package com.example.wwartService.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CommentTest {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Test
    public void testCreateValidComment() {
        // given
        final LocalDateTime createdDate = LocalDateTime.parse("2024-01-01 14:30:00", formatter);
        final Comment comment = new Comment("P01", "Jan Kowalski", "Świetny produkt!", 5, createdDate);

        // when
        // then
        assertNotNull(comment);
        assertEquals("P01", comment.getProductId());
        assertEquals("Jan Kowalski", comment.getAuthor());
        assertEquals("Świetny produkt!", comment.getText());
        assertEquals(5, comment.getRating());
        assertEquals(createdDate, comment.getCreatedDate());
    }

    @Test
    public void testCreateCommentWithNullProductIdThrows() {
        // given
        final LocalDateTime createdDate = LocalDateTime.parse("2024-01-01 14:30:00", formatter);

        // when
        final IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            new Comment(null, "Jan Kowalski", "Komentarz", 4, createdDate);
        });

        // then
        assertEquals("Product id cannot be null or empty.", thrown.getMessage());
    }

    @Test
    public void testCreateCommentWithEmptyAuthorThrows() {
        // given
        final LocalDateTime createdDate = LocalDateTime.parse("2024-01-01 14:30:00", formatter);

        // when
        final IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            new Comment("P01", "", "Komentarz", 4, createdDate.toString());
        });

        // then
        assertEquals("Author cannot be null or empty.", thrown.getMessage());
    }

    @Test
    public void testCreateCommentWithNullTextThrows() {
        // given
        final LocalDateTime createdDate = LocalDateTime.parse("2024-01-01 14:30:00", formatter);

        // when
        final IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            new Comment("P01", "Jan Kowalski", null, 4, createdDate.toString());
        });
        // then
        assertEquals("Text cannot be null or empty.", thrown.getMessage());
    }

    @Test
    public void testCreateCommentWithNullRatingThrows() {
        // given
        final LocalDateTime createdDate = LocalDateTime.parse("2024-01-01 14:30:00", formatter);

        // when
        final IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            new Comment("P01", "Jan Kowalski", "Komentarz", null, createdDate.toString());
        });

        // then
        assertEquals("Rating cannot be null or empty.", thrown.getMessage());
    }

    @Test
    public void testCreateCommentWithNullCreatedDateThrows() {
        // given
        // when
        final IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            new Comment("P01", "Jan Kowalski", "Komentarz", 4, "");
        });

        // then
        assertEquals("Created date cannot be null or empty.", thrown.getMessage());
    }

    @Test
    public void testGenerateProductIdValid() {
        // given
        // when
        // then
        assertEquals("P01", Comment.generateProductId(1));
        assertEquals("P09", Comment.generateProductId(9));
        assertEquals("P99", Comment.generateProductId(99));
    }

    @Test
    public void testGenerateProductIdInvalidLess() {
        // given
        // when
        final IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            Comment.generateProductId(-5);
        });

        // then
        assertEquals("Product number must be greater than 0", thrown.getMessage());
    }


    @Test
    public void testValidateProductIdNull() {

        // given
        // when
        final IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            Comment.validateProductId(null);
        });

        // then
        assertEquals("ProductId cannot be null or empty.", thrown.getMessage());
    }

    @Test
    public void testValidateProductIdInvalidFormatThrows() {
        // given
        // when
        final IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            Comment.validateProductId("PX1");
        });

        // then
        assertEquals("Invalid Product ID format. Correct format: P01, P02, ..., P99", thrown.getMessage());
    }
}
