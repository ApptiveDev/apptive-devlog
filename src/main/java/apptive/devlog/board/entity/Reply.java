package apptive.devlog.board.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDate;

@Entity
public class Reply {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    //comment foreign key
    private Long commentId;

    private LocalDate createdAt;
    private LocalDate updatedAt;
    private Long likeCount;
    private Boolean isDeleted;
}
