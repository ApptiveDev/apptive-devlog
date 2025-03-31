package apptive.devlog.Member.Repository;

import apptive.devlog.Global.Auth.Attribute.Provider;
import apptive.devlog.Member.Domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member,Long> {
    Optional<Member> findByEmail(String email);
    Optional<Member> findByNickname(String nickname);

    Optional<Member> findByEmailAndProvider(String email, Provider provider);

}
