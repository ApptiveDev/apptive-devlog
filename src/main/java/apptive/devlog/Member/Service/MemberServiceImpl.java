package apptive.devlog.Member.Service;


import apptive.devlog.Global.Auth.Dto.SignUpDto;
import apptive.devlog.Global.Auth.Jwt.JwtTokenProvider;
import apptive.devlog.Global.Auth.Service.AuthService;
import apptive.devlog.Global.Auth.Validator.PasswordValidator;
import apptive.devlog.Global.Exception.EmailDuplicationException;
import apptive.devlog.Global.Exception.InvalidTokenException;
import apptive.devlog.Global.Exception.MemberNotExistException;
import apptive.devlog.Global.Exception.NicknameDuplicationException;
import apptive.devlog.Member.Domain.Member;
import apptive.devlog.Member.Dto.UpdateProfileDto;
import apptive.devlog.Member.Dto.UpdateProfileRequest;
import apptive.devlog.Member.Repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService{
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordValidator passwordValidator;

    private final JwtTokenProvider jwtTokenProvider;

    private final AuthService authService;

    //private static final Logger logger = LoggerFactory.getLogger(AuthService.class);


    // parseDate 메소드: 문자열을 LocalDate로 파싱
    @Override
    public LocalDate parseDate(String birthDateStr) {
        try {
            return LocalDate.parse(birthDateStr); // yyyy-MM-dd 형식
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format", e);
        }
    }

    @Override
    public boolean checkInput(SignUpDto dto) {
        boolean check;
        try {
            check = dto.getEmail() != null && dto.getPassword() != null &&
                    dto.getName() != null && dto.getNickname() != null &&
                    dto.getBirth() != null && dto.getGender() != null;
        }catch (NullPointerException e){
            throw new IllegalArgumentException();
        }
        return check;

    }

    @Override
    public Member signUp(SignUpDto signUpDto) {
        if (!checkInput(signUpDto)) {
            throw new IllegalArgumentException("입력값 누락");
        }

        if (memberRepository.findByEmail(signUpDto.getEmail()).isPresent()) {
            // 이메일이 이미 존재하는 경우, 예외 발생
            throw new EmailDuplicationException();
        }


        if (!passwordValidator.isValid(signUpDto.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 유효하지 않습니다.");
        }

        if (memberRepository.findByNickname(signUpDto.getNickname()).isPresent()) {
            throw new NicknameDuplicationException();
        }

        // 날짜 파싱 작업을 서비스에서 처리
        LocalDate birthDate = parseDate(signUpDto.getBirth());
        String encodePwd = passwordEncoder.encode(signUpDto.getPassword());
        Member newMember = new Member(signUpDto.getEmail(),signUpDto.getName(),signUpDto.getNickname(),birthDate,signUpDto.getGender(),encodePwd);
        return memberRepository.save(newMember);
    }

    @Transactional
    @Override
    public void updateProfile(UpdateProfileDto request) throws AccessDeniedException {
        // 1. 토큰 검증 후 유저 조회
        if (!jwtTokenProvider.isValidToken(request.getToken())) {
            throw new InvalidTokenException();
        }

        // getCurrentMember()는 Optional<Member>를 반환하므로, 값이 없으면 예외를 던짐
        Member member = authService.getCurrentMember(request.getToken())
                .orElseThrow(() -> new MemberNotExistException());

        // 2. 닉네임과 비밀번호가 공백인지 확인
        if (isBlank(request.getNickname()) || isBlank(request.getPassword())) {
            //System.out.println("중복된 닉네임 존재 확인됨, 예외 던질 예정");
            throw new IllegalArgumentException("수정된 정보는 공백일 수 없습니다.");
        }

        // 3. 닉네임이 변경되었고, 중복된 닉네임인지 확인
        if (!member.getNickname().equals(request.getNickname())) {
            // 중복된 닉네임이 존재하는지 확인
            if(memberRepository.findByNickname(request.getNickname()).isPresent())
                throw new NicknameDuplicationException();
            member.setNickname(request.getNickname());  // 닉네임 변경
        }

        // 4. 비밀번호 유효성 검사
        validatePassword(request.getPassword());

        // 5. 새로운 비밀번호를 암호화하여 저장
        member.setPassword(passwordEncoder.encode(request.getPassword()));

        // 6. 변경된 정보 저장
        memberRepository.save(member);
    }


    private boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }

    private void validatePassword(String password) {
        if (password.length() < 10) {
            throw new IllegalArgumentException("비밀번호는 10자 이상이어야 합니다.");
        }
        if (!password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$")) {
            throw new IllegalArgumentException("비밀번호는 대소문자와 특수문자를 포함해야 합니다.");
        }
    }
}


