package apptive.devlog.Global.Auth.Service;

import apptive.devlog.Global.Auth.Dto.LoginRequestDto;
import apptive.devlog.Global.Auth.Jwt.JwtTokenProvider;
import apptive.devlog.Global.Exception.InvalidRequestException;
import apptive.devlog.Global.Exception.InvalidTokenException;
import apptive.devlog.Global.Exception.MemberAlreadyLogoutException;
import apptive.devlog.Global.Exception.MemberNotExistException;
import apptive.devlog.Member.Domain.Member;
import apptive.devlog.Member.Repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
//import org.slf4j.LoggerFactory;
//import org.slf4j.Logger;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService{

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    //private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    // 로그인
    @Override
    public Optional<Member> login(LoginRequestDto requestDto) {
        Member member = memberRepository.findByEmail(requestDto.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 이메일입니다."));
        if (!passwordEncoder.matches(requestDto.getPassword(), member.getPassword())) {
            throw new BadCredentialsException("비밀번호가 일치하지 않습니다.");
        }

        return Optional.of(member);
    }

    // 로그아웃
    @Override
    public boolean logout(String token) throws MemberAlreadyLogoutException{
        if (token == null || !jwtTokenProvider.isValidToken(token)) {
            throw new MemberAlreadyLogoutException();
        }

        jwtTokenProvider.invalidateToken(token);
        return true;
    }

    // 탈퇴
    @Override
    public boolean withdrawal(String token) throws InvalidTokenException{
        if (token == null || !jwtTokenProvider.isValidToken(token)) {
            throw new InvalidTokenException();
        }

        //이미 getCurrentMember에서 예외를 처리하는데 여기서 또 해야하나..?
        // getCurrentMember()는 Optional<Member>를 반환하므로, 값을 꺼내는 안전한 방법으로 처리
        Member member = getCurrentMember(token)
                .orElseThrow(MemberNotExistException::new);  // 값이 없으면 예외 던짐

        // 이후 게시글, 댓글 관련 로직을 넣을 수 있음
        // postRepository.updateAuthorToDeletedUser(member.getId());
        // commentRepository.updateAuthorToDeletedUser(member.getId());

        memberRepository.delete(member);  // 실제 계정 삭제
        jwtTokenProvider.invalidateToken(token);  // 토큰 무효화
        return true;
    }


    // 현재 로그인한 유저 정보 추출
    public Optional<Member> getCurrentMember(String token) {
        String email = jwtTokenProvider.getEmailFromToken(token);
        return Optional.of(memberRepository.findByEmail(email)
                .orElseThrow(MemberNotExistException::new));
    }

}

