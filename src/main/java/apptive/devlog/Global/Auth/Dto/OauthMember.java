package apptive.devlog.Global.Auth.Dto;

import apptive.devlog.Global.Auth.Attribute.Provider;
import apptive.devlog.Member.Domain.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OauthMember {
    private String name; // 사용자 이름
    private Provider provider; // 로그인한 서비스
    private String email; // 사용자의 이메일

    // DTO 파일을 통하여 Entity를 생성하는 메소드
    public Member toEntity() {
        return Member.builder()
                .name(this.name)
                .email(this.email)
                .provider(this.provider)
                .build();
    }
}
