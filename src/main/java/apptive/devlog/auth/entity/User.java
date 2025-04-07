package apptive.devlog.auth.entity;

import apptive.devlog.auth.dto.UserLoginForm;
import apptive.devlog.auth.dto.UserSaveForm;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
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

    public User() {}

    public User(String email, String password, String name, String nickname, LocalDate birth, Gender gender){
        this.email = email;
        this.password = password;
        this.name = name;
        this.nickname = nickname;
        this.birth = birth;
        this.gender = gender;
    }

    public User(UserLoginForm form){
        this.email = form.getEmail();
        this.password = form.getPassword();
    }

    public User(UserSaveForm form){
        this.email = form.getEmail();
        this.password = form.getPassword();
        this.name = form.getName();
        this.nickname = form.getNickname();
        this.birth = form.getBirth();
        this.gender = form.getGender();
    }

}
