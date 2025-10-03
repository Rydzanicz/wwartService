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
    private String photoPath;

    public Comment() {
        this.productId = generateProductId(1);
        this.author = null;
        this.text = null;
        this.rating = null;
        this.createdDate = LocalDateTime.now();
        this.photoPath = null;
    }

    public Comment(final String productId, final String author, final String text, final Integer rating, final String createdDate, final String photoPath) {
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
        this.photoPath = photoPath;
    }

    public Comment(final Integer productId, final String author, final String text, final Integer rating, final LocalDateTime createdDate, final String photoPath) {
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
        this.productId = generateProductId(productId);
        this.author = author;
        this.text = text;
        this.rating = rating;
        this.createdDate = createdDate;
        this.photoPath = photoPath;
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
        this.photoPath = "";
    }

    public Comment(final Integer productId, final String author, final String text, final Integer rating) {
        if (productId == null || productId == 0) {
            throw new IllegalArgumentException("Product id cannot be null or empty.");
        }
        if (author == null || author.isEmpty()) {
            throw new IllegalArgumentException("Author cannot be null or empty.");
        }
        if (text == null || text.isEmpty()) {
            throw new IllegalArgumentException("Text cannot be null or empty.");
        }
        if (rating == null || rating == 0) {
            throw new IllegalArgumentException("Rating cannot be null or empty.");
        }
        this.productId = generateProductId(productId);
        this.author = author;
        this.text = text;
        this.rating = rating;
        this.createdDate = LocalDateTime.now();
        this.photoPath = "";
    }

    public Comment(final CommentEntity comment) {

        validateProductId(comment.getProductId());
        this.productId = comment.getProductId();
        this.author = comment.getAuthor();
        this.text = comment.getText();
        this.rating = comment.getRating();
        this.createdDate = LocalDateTime.parse(comment.getCreatedDate(), formatter);
        this.photoPath = comment.getPhotoPath();
    }

    public static String generateProductId(final String numberStr) {
        if (numberStr == null || numberStr.isBlank()) {
            throw new IllegalArgumentException("Product number cannot be null or blank");
        }
        try {
            final int number = Integer.parseInt(numberStr);
            if (number <= 0) {
                throw new IllegalArgumentException("Product number must be greater than 0");
            }
            return String.format(FORMAT, number);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Product number must be a valid integer", e);
        }
    }


    public static String generateProductId(final int number) {
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

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(final String photoPath) {
        this.photoPath = photoPath;
    }
}
