package apptive.devlog.comment.dto;


import apptive.devlog.domain.Comment;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentResponse {

    private Long id;
    private String author;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<ReCommentResponse> reComments = new ArrayList<>();
    private Boolean isDeleted;

   

    public CommentResponse(String author, Comment comment, List<ReCommentResponse> reComments) {
        this.author = author;
        this.id = comment.getId();
        this.content = comment.getContent();
        this.createdAt = comment.getCreatedAt();
        this.updatedAt = comment.getUpdatedAt();
        this.isDeleted = comment.isDeleted();
        this.reComments = reComments;
    }

    public CommentResponse(String author, Comment comment) {
        this.author = author;
        this.id = comment.getId();
        this.content = comment.getContent();
        this.createdAt = comment.getCreatedAt();
        this.updatedAt = comment.getUpdatedAt();
        this.isDeleted = comment.isDeleted();
    }

}

