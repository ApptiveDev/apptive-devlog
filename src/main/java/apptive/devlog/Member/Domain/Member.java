package apptive.devlog.Member.Domain;

import apptive.devlog.Global.Auth.Attribute.Provider;
import apptive.devlog.Global.Enum.Gender;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Objects;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", nullable = false)
    private String email;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "nickname", nullable = false)
    private String nickname;
    @Column(name = "birth", nullable = false)
    private LocalDate birth;
    @Column(name = "gender", nullable = false)
    private Gender gender;
    @Column(name = "password")
    private String password;

    @Column(name = "provider", nullable = false)
    private Provider provider;

    public Member(String email, String name, String nickname, LocalDate birth, Gender gender, String encodePwd) {
        this.email = email;
        this.name = name;
        this.nickname = nickname;
        this.birth = birth;
        this.gender = gender;
        this.provider = Provider.DEVLOG;
    }

    // 사용자의 이름이나 이메일을 업데이트하는 메소드
    public Member updateMember(String name, String email) {
        this.name = name;
        this.email = email;

        return this;
    }

    // equals & hashCode 오버라이드 필요 (테스트 비교에서 사용되므로)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Member)) return false;
        Member member = (Member) o;
        return Objects.equals(email, member.email) &&
                Objects.equals(password, member.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, password);
    }
}

