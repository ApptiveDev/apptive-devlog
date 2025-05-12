package apptive.devlog.member.repository;

import apptive.devlog.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email); //timeout, 인덱스 고려
    Optional<Member> findByNickname(String nickname); //timeout, 인덱스 고려


    @Query("select m from Member m left join fetch m.uploadFiles where m.email = :email")
    Optional<Member> findByEmailWithFiles(String email);
}
