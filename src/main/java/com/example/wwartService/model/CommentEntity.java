package com.example.wwartService.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "comments")
public class CommentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id", nullable = false)
    private String productId;

    @Column(nullable = false)
    private String author;

    @Column(length = 2000, nullable = false)
    private String text;

    @Column(nullable = false)
    private Integer rating;

    @Column(name = "created_date", nullable = false)
    private String createdDate;

    public CommentEntity() {}

    public CommentEntity(final String productId, final String author, final String text, final Integer rating, final String createdDate) {
        this.productId = productId;
        this.author = author;
        this.text = text;
        this.rating = rating;
        this.createdDate = createdDate;
    }

    public CommentEntity(final Comment comment) {
        this.productId = comment.getProductId();
        this.author = comment.getAuthor();
        this.text = comment.getText();
        this.rating = comment.getRating();
        this.createdDate = comment.getCreatedDate()
                                  .format(comment.getFormatter());
    }


    public Long getId() {
        return id;
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

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(final String createdDate) {
        this.createdDate = createdDate;
    }
}
