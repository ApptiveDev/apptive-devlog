package apptive.devlog.Post.Repository;

import apptive.devlog.Member.Domain.Member;
import apptive.devlog.Post.Domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    Optional<Post> findById(Long postId);

    List<Post> findByTitleContaining(String title);

    List<Post> findByContentContaining(String content);

    List<Post> findByTitleContainingAndContentContaining(String title, String content);

    List<Post> findByAuthor(Optional<Member> author);
} 