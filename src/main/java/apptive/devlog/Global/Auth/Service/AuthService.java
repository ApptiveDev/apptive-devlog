package apptive.devlog.Global.Auth.Service;

import apptive.devlog.Global.Auth.Dto.LoginRequestDto;
import apptive.devlog.Member.Domain.Member;

import java.util.Optional;

public interface AuthService {
    Optional<Member> login(LoginRequestDto loginRequestDto);

    boolean logout(String validToken);

    Optional<Member> getCurrentMember(String validToken);

    boolean withdrawal(String validToken);
}

