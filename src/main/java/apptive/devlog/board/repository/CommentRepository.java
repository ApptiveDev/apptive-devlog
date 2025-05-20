package apptive.devlog.board.repository;

import apptive.devlog.board.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByIsDeletedFalse();

    @Query("SELECT c From Comment c Where c.post.id = :postId AND c.isDeleted = false")
    List<Comment> findValidByPostId(@Param("postId") Long postId);
}
