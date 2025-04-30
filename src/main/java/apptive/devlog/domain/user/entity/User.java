package apptive.devlog.domain.user.entity;

import apptive.devlog.common.base.BaseEntity;
import apptive.devlog.domain.user.enums.Gender;
import apptive.devlog.domain.user.enums.Provider;
import apptive.devlog.domain.user.enums.Role;
import de.huxhorn.sulky.ulid.ULID;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Table(name = "users")
public class User extends BaseEntity {
    @Column(nullable = false, unique = true)
    private String email;

    private String nickname;

    @Column(nullable = false)
    private String name;

    private LocalDate birth;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String password;

    private String providerId;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "user_providers", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Set<Provider> providers;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    public User(String email, String name, Provider provider, Role role) {
        this.email = email;
        this.name = name;
        this.role = role;
        this.providers = new HashSet<>(Collections.singletonList(provider));
    }

    public void addProvider(Provider provider) {
        if (providers == null) { this.providers = new HashSet<>(); }
        this.providers.add(provider);
    }

    public static User of(String email, String name, Provider provider, Role role) {
        return new User(email, name, provider, role);
    }
}
