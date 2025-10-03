package com.example.wwartService.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

public class Comment {
    private static final String PRODUCT_ID_PATTERN = "^P\\d{2}$";
    private static final String FORMAT = "P%02d";
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private String productId;
    private String author;
    private String text;
    private Integer rating;
    private LocalDateTime createdDate;

    public Comment() {
        this.productId = generateProductId(1);
        this.author = null;
        this.text = null;
        this.rating = null;
        this.createdDate = LocalDateTime.now();

    }

    public Comment(final String productId, final String author, final String text, final Integer rating, final String createdDate) {
        if (productId == null || productId.describeConstable().isEmpty()) {
            throw new IllegalArgumentException("Product id cannot be null or empty.");
        }
        if (author == null || author.isEmpty()) {
            throw new IllegalArgumentException("Author cannot be null or empty.");
        }
        if (text == null || text.isEmpty()) {
            throw new IllegalArgumentException("Text cannot be null or empty.");
        }
        if (rating == null || rating.describeConstable().isEmpty()) {
            throw new IllegalArgumentException("Rating cannot be null or empty.");
        }
        if (createdDate == null || createdDate.isEmpty()) {
            throw new IllegalArgumentException("Created date cannot be null or empty.");
        }
        this.productId = productId;
        this.author = author;
        this.text = text;
        this.rating = rating;
        this.createdDate = LocalDateTime.parse(createdDate, formatter);
    }

    public Comment(final String productId, final String author, final String text, final Integer rating, final LocalDateTime createdDate) {
        if (productId == null || productId.describeConstable().isEmpty()) {
            throw new IllegalArgumentException("Product id cannot be null or empty.");
        }
        if (author == null || author.isEmpty()) {
            throw new IllegalArgumentException("Author cannot be null or empty.");
        }
        if (text == null || text.isEmpty()) {
            throw new IllegalArgumentException("Text cannot be null or empty.");
        }
        if (rating == null || rating.describeConstable().isEmpty()) {
            throw new IllegalArgumentException("Rating cannot be null or empty.");
        }
        if (createdDate == null) {
            throw new IllegalArgumentException("Created date cannot be null or empty.");
        }
        this.productId = productId;
        this.author = author;
        this.text = text;
        this.rating = rating;
        this.createdDate = createdDate;
    }

    public Comment(final CommentEntity comment) {

        validateProductId(comment.getProductId());
        this.productId = comment.getProductId();
        this.author = comment.getAuthor();
        this.text = comment.getText();
        this.rating = comment.getRating();
        this.createdDate = LocalDateTime.parse(comment.getCreatedDate(), formatter);
    }

    public static String generateProductId(int number) {
        if (number <= 0) {
            throw new IllegalArgumentException("Product number must be greater than 0");
        }
        return String.format(FORMAT, number);
    }

    public static boolean validateProductId(final String productId) {
        if (productId == null || productId.isEmpty()) {
            throw new IllegalArgumentException("ProductId cannot be null or empty.");
        }
        if (!Pattern.matches(PRODUCT_ID_PATTERN, productId)) {
            throw new IllegalArgumentException("Invalid Product ID format. Correct format: P01, P02, ..., P99");
        }
        return true;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(final String productId) {
        this.productId = productId;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(final String author) {
        this.author = author;
    }

    public String getText() {
        return text;
    }

    public void setText(final String text) {
        this.text = text;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(final Integer rating) {
        this.rating = rating;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(final LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public DateTimeFormatter getFormatter() {
        return formatter;
    }
}
