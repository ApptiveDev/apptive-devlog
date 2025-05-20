package apptive.devlog.board.repository;

import apptive.devlog.auth.entity.User;
import apptive.devlog.board.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByIsDeletedFalse();
    List<Post> findByUserId(Long userId);
}
