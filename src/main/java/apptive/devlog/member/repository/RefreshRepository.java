package apptive.devlog.member.repository;

import apptive.devlog.domain.RefreshEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface RefreshRepository extends JpaRepository<RefreshEntity, Long> {

    Boolean existsByEmail(String email);

    Boolean existsByRefresh(String refresh);

    @Transactional
    void deleteByRefresh(String refresh);

    @Transactional
    void deleteByEmail(String email);
}
