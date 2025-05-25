package apptive.devlog.domain;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Member extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    private String email;

    private String password;

    private String username;

    private String nickname;

    private LocalDate birthdate;

    private String role;

    @Enumerated(EnumType.STRING)
    private Gender gender; //enum 값 검증 필요

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UploadFile> uploadFiles = new ArrayList<>();

    @OneToMany(mappedBy = "toMember", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Follow> followerList = new ArrayList<>();

    @OneToMany(mappedBy = "fromMember", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Follow> followingList = new ArrayList<>();


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
