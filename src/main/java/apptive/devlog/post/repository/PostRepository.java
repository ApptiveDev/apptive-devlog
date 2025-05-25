package apptive.devlog.post.repository;

import apptive.devlog.domain.Member;
import apptive.devlog.domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("select distinct p from Post p left join fetch p.files join fetch p.member where p.id = :id")
    Optional<Post> findWithFiles(Long id);

    @Query("select p from Post p join fetch p.member where p.id = :id")
    Optional<Post> findWithMember(Long id);
}
