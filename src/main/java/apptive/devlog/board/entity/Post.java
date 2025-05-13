package apptive.devlog.board.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter @Setter
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    //postData foreign key
    private Long dataId;

    private LocalDate createdAt;
    private LocalDate updatedAt;
    private Long likeCount;
    private Boolean isDeleted;

}
