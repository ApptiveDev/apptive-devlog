package apptive.devlog.follow.repository;

import apptive.devlog.domain.Follow;
import apptive.devlog.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    @Query("select f from Follow f join fetch f.fromMember where f.toMember = :member and f.isAccepted = false")
    List<Follow> findRequests(Member member);

    @Query("select f from Follow f join fetch  f.fromMember join fetch f.toMember where f.id = :followId")
    Optional<Follow> findByIdWithMember(Long followId);

    @Query("select f from Follow f join fetch f.fromMember where f.toMember = :member and f.isAccepted=true")
    List<Follow> findFollowers(Member member);

    @Query("select f from Follow f join fetch f.toMember where f.fromMember = :member and f.isAccepted=true ")
    List<Follow> findFollowings(Member member);

    @Query("select f from Follow f where f.toMember = :toMember and f.fromMember = :fromMember")
    Optional<Follow> findByMembers(Member toMember, Member fromMember);
}
