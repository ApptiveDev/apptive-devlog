package apptive.devlog.domain.comment.entity;

import apptive.devlog.common.base.BaseEntity;
import apptive.devlog.domain.post.entity.Post;
import apptive.devlog.domain.user.entity.User;
import de.huxhorn.sulky.ulid.ULID;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Table(name = "comments")
public class Comment extends BaseEntity {
    @NotBlank
    @Column(columnDefinition = "TEXT")
    private String text;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Comment> replies;

    @Column(nullable = false)
    private boolean deleted;

    @Transient
    private int depth;

    public void updateText(String newText) {
        this.text = newText;
    }
}
