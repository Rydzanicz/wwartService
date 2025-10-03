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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/comments")
public class CommentsController {

    private static final Logger logger = LoggerFactory.getLogger(CommentsController.class);

    private final CommentsService commentService;

    public CommentsController(final CommentsService commentService) {
        this.commentService = commentService;
    }

    @GetMapping
    public ResponseEntity<List<Comment>> getComments(@RequestParam String productId) {
        try {
            if (productId == null || productId.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            final List<Comment> comments = commentService.getCommentsByProductId(Comment.generateProductId(productId));
            logger.info("Pobrano {} komentarzy dla produktu: {}", comments.size(), productId);
            return ResponseEntity.ok(comments);

        } catch (Exception e) {
            logger.error("Błąd podczas pobierania komentarzy dla produktu {}: {}", productId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Comment> addComment(@RequestParam Integer productId, @RequestParam String author, @RequestParam String text, @RequestParam Integer rating, @RequestParam(required = false) MultipartFile image) {

        try {
            if (productId == null || productId == 0) {
                logger.error("ProductId nie może być pusty");
                return ResponseEntity.badRequest().build();
            }
            LocalDateTime.now();
            final Comment newComment = new Comment(productId, author, text, rating);
            commentService.saveComment(newComment, image);
            logger.info("Dodano komentarz  dla produktu: {}", productId);
            return ResponseEntity.status(HttpStatus.CREATED).body(newComment);

        } catch (IllegalArgumentException e) {
            logger.error("Nieprawidłowe dane wejściowe: {}", e.getMessage());
            return ResponseEntity.badRequest().build();

        } catch (Exception e) {
            logger.error("Błąd podczas dodawania komentarza: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}