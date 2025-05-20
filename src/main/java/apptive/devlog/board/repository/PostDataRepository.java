package apptive.devlog.board.repository;

import apptive.devlog.auth.entity.User;
import apptive.devlog.board.entity.PostData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostDataRepository extends JpaRepository<PostData, Long> {
}
