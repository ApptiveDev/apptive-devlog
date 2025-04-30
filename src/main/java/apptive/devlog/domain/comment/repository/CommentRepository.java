package apptive.devlog.domain.comment.repository;

import apptive.devlog.domain.comment.entity.Comment;
import apptive.devlog.domain.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, String> {
    List<Comment> findByPostIdAndParentIsNullOrderByCreatedAtDesc(String postId);
    List<Comment> findByParentIdOrderByCreatedAtDesc(String parentId);

    List<Comment> findByPostAndParentIsNull(Post post);
    Optional<Comment> findByIdAndDeletedFalse(String id);

    String post(Post post);
}
