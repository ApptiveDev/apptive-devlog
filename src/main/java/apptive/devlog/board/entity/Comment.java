package apptive.devlog.board.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    //post foreign key
    private Long postId;
    private LocalDate createdAt;
    private LocalDate updatedAt;
    private Long likeCount;
    private Boolean isDeleted;
}
