package apptive.devlog.auth.entity;

import apptive.devlog.auth.dto.UserLoginForm;
import apptive.devlog.auth.dto.UserSaveForm;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
// springSecurity user 인증 정보와 테이블 겹쳐서 수정
@Table(name = "users")
@Getter @Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String nickname;


    private LocalDate birth;

    private Gender gender;

    @Column(nullable = true)
    private String role;

    public User() {}

    public User(String email, String password, String name, String nickname, LocalDate birth, Gender gender, String role){
        this.email = email;
        this.password = password;
        this.name = name;
        this.nickname = nickname;
        this.birth = birth;
        this.gender = gender;
        this.role = role;
    }

    // role 기본 값 설정
    @PrePersist
    public void prePersist() {
        if (this.role == null) {
            this.role = "user";
        }
    }
}
