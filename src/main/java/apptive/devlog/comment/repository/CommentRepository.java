package apptive.devlog.comment.repository;

import apptive.devlog.comment.dto.ReCommentResponse;
import apptive.devlog.domain.Comment;
import apptive.devlog.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("select count (c) from Comment c where c.parent = :parent")
    long countChild(Comment parent);

    @Query("select c from Comment c join fetch c.member where c.id = :commentId")
    Optional<Comment> findWithMember(@Param("commentId") Long commentId);

    @Query("select new apptive.devlog.comment.dto.ReCommentResponse " +
            "(c.id, c.member.nickname, c.content,c.parent.id, c.createdAt, c.updatedAt) from Comment c where c.parent in :parents")
    List<ReCommentResponse> findReComments(List<Comment> parents);


}
