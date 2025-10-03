package com.example.wwartService.controler;

import com.example.wwartService.model.Comment;
import com.example.wwartService.service.CommentsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/comments")
public class CommentsController {

    private static final Logger logger = LoggerFactory.getLogger(CommentsController.class);
    private final CommentsService commentsService;

    public CommentsController(final CommentsService commentsService) {
        this.commentsService = commentsService;
    }


    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Comment>> getComments(@RequestParam() String productId) {
        if (productId == null) {
            throw new IllegalArgumentException("Invalid request payload");
        }

        try {
            List<Comment> comments = new ArrayList<>();

            if (productId != null && !productId.isEmpty()) {
                comments = commentsService.getCommentsByProductId(productId);
            }

            if (comments == null || comments.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(comments);

        } catch (Exception e) {
            logger.error("Błąd podczas pobierania komentarzy", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Comment> addComment(@RequestBody CommentRequest commentRequest) {
        try {
            final Comment comment = new Comment(commentRequest.getProductId(), commentRequest.getAuthor(), commentRequest.getText(),
                                                commentRequest.getRating(), commentRequest.getCreatedDate());

            commentsService.saveComment(comment);
            return ResponseEntity.status(HttpStatus.CREATED).body(comment);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}