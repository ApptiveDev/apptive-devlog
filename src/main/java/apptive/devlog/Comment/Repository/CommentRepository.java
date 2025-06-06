package apptive.devlog.Comment.Repository;

import apptive.devlog.Comment.Domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    
    // 특정 게시글의 최상위 댓글들을 조회
    @Query("SELECT c FROM Comment c WHERE c.post.id = :postId AND c.parent IS NULL ORDER BY c.createdAt DESC")
    List<Comment> findRootCommentsByPostId(@Param("postId") Long postId);

    // 특정 댓글의 모든 대댓글을 조회
    @Query("SELECT c FROM Comment c WHERE c.parent.id = :parentId ORDER BY c.createdAt ASC")
    List<Comment> findChildrenByParentId(@Param("parentId") Long parentId);

    // 특정 게시글의 모든 댓글을 조회 (계층 구조 포함)
    @Query("SELECT c FROM Comment c WHERE c.post.id = :postId ORDER BY c.createdAt DESC")
    List<Comment> findAllCommentsByPostId(@Param("postId") Long postId);
} 