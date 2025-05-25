package apptive.devlog.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class RefreshEntity extends BaseTimeEntity{

    @Id @GeneratedValue
    Long id;

    private String email;

    private String refresh;

    private String expiration;

    protected RefreshEntity() {}

    public RefreshEntity(String email, String refresh, String expiration) {
        this.email = email;
        this.refresh = refresh;
        this.expiration = expiration;
    }
}
