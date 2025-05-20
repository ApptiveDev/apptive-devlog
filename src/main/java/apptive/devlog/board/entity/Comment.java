package apptive.devlog.board.entity;

import apptive.devlog.auth.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter @Setter
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    private LocalDate createdAt;
    private LocalDate updatedAt;
    private Long likeCount;
    private Boolean isDeleted;

    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL)
    private List<Reply> replyList;
}
