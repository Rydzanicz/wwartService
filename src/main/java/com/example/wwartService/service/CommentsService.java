package com.example.wwartService.service;

import com.example.wwartService.model.Comment;
import com.example.wwartService.model.CommentEntity;
import com.example.wwartService.repository.CommentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class CommentsService {

    private static final Logger logger = LoggerFactory.getLogger(CommentsService.class);

    private final CommentRepository commentRepository;
    private final GoogleCloudStorageService  googleCloudStorageService;

    public CommentsService(final CommentRepository commentRepository, final GoogleCloudStorageService  googleCloudStorageService) {
        this.commentRepository = commentRepository;
        this.googleCloudStorageService = googleCloudStorageService;
    }

    public List<Comment> getCommentsByProductId(final String productId) {
        final List<CommentEntity> entities = commentRepository.findCommentsByProductId(productId);
        return entities.stream().map(this::mapToComment).toList();
    }

    public Comment saveComment(final Comment comment, final MultipartFile image) {

        String imageUrl = null;
        if (image != null && !image.isEmpty()) {
            try {
                imageUrl = googleCloudStorageService.uploadFile(image);
                logger.info("Zapisano zdjęcie dla komentarza: {}", imageUrl);
            } catch (IOException e) {
                logger.error("Błąd podczas zapisywania zdjęcia: {}", e.getMessage());
                throw new RuntimeException("Błąd podczas zapisywania zdjęcia: " + e.getMessage());
            }
        }
        comment.setPhotoPath(imageUrl);
        final CommentEntity entity = new CommentEntity(comment);
        CommentEntity savedEntity = commentRepository.save(entity);

        logger.info("Dodano komentarz o ID: {} dla produktu: {}", savedEntity.getId(), comment.getProductId());
        return new Comment(savedEntity);
    }

    private Comment mapToComment(final CommentEntity entity) {
        return new Comment(entity.getProductId(), entity.getAuthor(), entity.getText(), entity.getRating(), entity.getCreatedDate(),entity.getPhotoPath());

    }
}