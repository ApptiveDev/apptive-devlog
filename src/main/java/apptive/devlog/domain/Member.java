package apptive.devlog.domain;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Member {

    @Id @GeneratedValue
    private Long id;

    private String email;

    private String password;

    private String username;

    private String nickname;

    private LocalDate birthdate;

    private String role;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    public Member(String email, String password, String username, String nickname, LocalDate birthdate, String role, Gender gender) {
        this.email = email;
        this.password = password;
        this.username = username;
        this.nickname = nickname;
        this.birthdate = birthdate;
        this.role = role;
        this.gender = gender;
    }

    public Member(String email, String role) {
        this.email = email;
        this.role = role;
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void updateBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
    }

    public void updateGender(Gender gender) {
        this.gender = gender;
    }

    public void updatePassword(String password) {
        this.password = password;
    }
}
