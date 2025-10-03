package com.example.wwartService.service;

import com.example.wwartService.model.Comment;
import com.example.wwartService.model.CommentEntity;
import com.example.wwartService.repository.CommentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentsService {
    private final CommentRepository commentRepository;

    public CommentsService(final CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }


    public List<Comment> getCommentsByProductId(final String productId) {
        final List<CommentEntity> entities = commentRepository.findCommentsByProductId(productId);
        return entities.stream()
                       .map(this::mapToComment)
                       .toList();
    }

    private Comment mapToComment(final CommentEntity entity) {
        return new Comment(entity.getProductId(),
                           entity.getAuthor(),
                           entity.getText(),
                           entity.getRating(),
                           entity.getCreatedDate());
    }

    public void saveComment(final Comment comment) {
        final CommentEntity commentEntity = new CommentEntity(comment);

        commentRepository.save(commentEntity);
    }
}