package apptive.devlog.Auth.Service;

import apptive.devlog.Global.Auth.Dto.LoginRequestDto;
import apptive.devlog.Global.Auth.Jwt.JwtTokenProvider;
import apptive.devlog.Global.Auth.Service.AuthService;
import apptive.devlog.Global.Auth.Service.AuthServiceImpl;
import apptive.devlog.Global.Auth.Validator.PasswordValidator;
import apptive.devlog.Global.Enum.Gender;
import apptive.devlog.Global.Exception.InvalidTokenException;
import apptive.devlog.Global.Exception.MemberAlreadyLogoutException;
import apptive.devlog.Global.Exception.MemberNotExistException;
import apptive.devlog.Member.Domain.Member;
import apptive.devlog.Member.Repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

// 궁금한 점 - 로그인 관련 테스트- 서비스 부분을 모두 여기서 진행하는데,
// 클래스를 세부기능별로 구분하는게 나을까?(로그인,회원가입...)

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private AuthService authService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordValidator passwordValidator;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private AuthServiceImpl authServiceImpl;

    private final String email = "test@example.com";
    private final String pwd = "signupTest1#";
    private final String name = "테스트";

    private final String nickname = "테스트닉네임";
    private final String birth = "2025-03-29";

    private final Gender gender = Gender.MALE;

    @Nested
    @DisplayName("패스워드 조건 테스트")
    class PasswordValidationTests {
        @BeforeEach
        void setUp() {
            passwordValidator = new PasswordValidator(); // 또는 @InjectMocks 사용 가능
        }

        @Test
        @DisplayName("길이 부족하면 실패, 최소 10자 이상이면 성공")
        void shouldValidatePasswordLength() {
            assertFalse(passwordValidator.isValid("short"));
            assertTrue(passwordValidator.isValid("longEnough1!"));
        }

        @Test
        @DisplayName("대문자 없으면 실패, 포함 시 성공")
        void shouldRequireUppercase() {
            assertFalse(passwordValidator.isValid("lowercase123!"));
            assertTrue(passwordValidator.isValid("Uppercase123!"));
        }

        @Test
        @DisplayName("소문자 없으면 실패, 포함 시 성공")
        void shouldRequireLowercase() {
            assertFalse(passwordValidator.isValid("UPPERCASE123!"));
            assertTrue(passwordValidator.isValid("Uppercase123!"));
        }

        @Test
        @DisplayName("숫자 없으면 실패, 포함 시 성공")
        void shouldRequireNumber() {
            assertFalse(passwordValidator.isValid("NoNumbers!"));
            assertTrue(passwordValidator.isValid("HasNumber1!"));
        }

        @Test
        @DisplayName("특수문자 없으면 실패, 포함 시 성공")
        void shouldRequireSpecialCharacter() {
            assertFalse(passwordValidator.isValid("NoSpecial123"));
            assertTrue(passwordValidator.isValid("WithSpecial@123"));
        }

        @Test
        @DisplayName("공백이 포함되면 실패")
        void shouldFailIfContainsWhitespace() {
            assertFalse(passwordValidator.isValid("With space1!"));
            assertTrue(passwordValidator.isValid("NoSpacewithlenght1!"));
        }

        @Test
        @DisplayName("모든 조건 만족 시 통과")
        void shouldPassIfAllConditionsMet() {
            assertTrue(passwordValidator.isValid("ValidPass123!"));
        }

        @Test
        @DisplayName("조건 하나라도 부족하면 실패")
        void shouldFailIfAnyConditionNotMet() {
            assertFalse(passwordValidator.isValid("invalid")); // 짧고 조건 부족
            assertFalse(passwordValidator.isValid("ValidPassword123")); // 특수문자 없음
        }
    }




    @Nested
    @DisplayName("로그인 테스트")
    class Login {
        @Mock
        private LoginRequestDto loginRequestDto;

        Member testMember;
        @BeforeEach
        void setUp() {
            MockitoAnnotations.openMocks(this);
        }

        @Test
        @DisplayName("로그인 성공 테스트")
        void loginSuccess(){
            // Given
            String encodePwd = passwordEncoder.encode(pwd);
            testMember = new Member(email,name,nickname,LocalDate.parse(birth),gender,encodePwd); // 가상의 멤버 객체 생성
            loginRequestDto = new LoginRequestDto(email,pwd); // 로그인 요청 DTO

            when(memberRepository.findByEmail(anyString())).thenReturn(Optional.of(testMember)); // 이메일로 멤버 조회 시 testMember 리턴
            when(passwordEncoder.matches(anyString(),any())).thenReturn(true);
            // When & Then
            Optional<Member> result = authServiceImpl.login(loginRequestDto); // 로그인 서비스 호출

            assertEquals(testMember, result.get()); // 결과가 예상한 testMember와 일치하는지 확인
        }

        @Test
        @DisplayName("로그인 실패 - 아이디 탐색이 불가한 경우")
        void loginFail_IdNotExist(){
            // Given
            loginRequestDto = new LoginRequestDto(email,pwd); // 로그인 요청 DTO

            when(memberRepository.findByEmail(email)).thenReturn(Optional.empty()); // 이메일로 멤버 조회 시 null 리턴

            // When & Then
            assertThrows(EntityNotFoundException.class, () -> authServiceImpl.login(loginRequestDto)); // 예외가 발생하는지 확인
        }

        @Test
        @DisplayName("로그인 실패 - 비밀번호가 일치하지 않은 경우")
        void loginFail_InvalidPw(){
            // Given
            String encodePwd = passwordEncoder.encode(pwd);
            testMember = new Member(email,name,nickname,LocalDate.parse(birth),gender,encodePwd); // 가상의 멤버 객체 생성
            loginRequestDto = new LoginRequestDto(email,pwd); // 로그인 요청 DTO

            when(memberRepository.findByEmail(anyString())).thenReturn(Optional.ofNullable(testMember)); // 이메일로 멤버 조회 시 testMember 리턴
            // 비밀번호 검증 로직을 mock해서 실패하도록 설정
            when(passwordEncoder.matches(any(),any())).thenReturn(false); // 비밀번호 검증 실패
            // When & Then
            assertThrows(BadCredentialsException.class, () -> authServiceImpl.login(loginRequestDto)); // 예외가 발생하는지 확인
        }


        //휴면 유저인 경우?!?
        //만든다면 최종 로그인 시간을 저장해야할듯!
    }

    @Nested
    @DisplayName("로그아웃 테스트")
    class Logout {

        @BeforeEach
        void setUp() {
            MockitoAnnotations.openMocks(this);
        }

        @Test
        @DisplayName("로그아웃 성공 - 로그인 상태에서 로그아웃 요청")
        void logoutSuccess() {
            // Given: 로그인 상태임을 설정 (예시로 토큰을 설정)
            String validToken = "valid-jwt-token";  // 로그인된 상태를 시뮬레이션
            when(jwtTokenProvider.isValidToken(validToken)).thenReturn(true);  // 유효한 토큰이라고 설정

            // When: 로그아웃 요청을 보냄
            authServiceImpl.logout(validToken);  // 로그아웃 성공 시 true 리턴

            // Then: 로그아웃 후 토큰 삭제 및 리디렉션
            verify(jwtTokenProvider).invalidateToken(validToken);  // 토큰 무효화 확인
            assertTrue(authServiceImpl.logout(validToken));  // 로그아웃 성공 여부 확인
        }

        @Test
        @DisplayName("로그아웃 실패 - 로그인되지 않은 상태에서 로그아웃 요청")
        void logoutFail_NotLoggedIn() {
            // Given: 로그인되지 않은 상태 (유효하지 않은 토큰)
            String invalidToken = "invalid-jwt-token";  // 로그인되지 않은 상태

            // When: 로그아웃 요청을 보냄
            when(jwtTokenProvider.isValidToken(invalidToken)).thenReturn(false);  // 토큰 유효하지 않음

            // Then: Invalid request 예외 발생
            assertThrows(MemberAlreadyLogoutException.class, () -> authServiceImpl.logout(invalidToken));  // 예외 발생 확인
        }

    }


    @Nested
    @DisplayName("탈퇴 테스트")
    class Withdrawal {
        Member testMember;

        @BeforeEach
        void setUp() {
            String encodePwd = passwordEncoder.encode("password");
            testMember = new Member(email, name, nickname, LocalDate.parse(birth, DateTimeFormatter.ISO_DATE), gender, encodePwd);
            MockitoAnnotations.openMocks(this);
        }

        @Test
        @DisplayName("탈퇴 성공 - 로그인한 유저가 동의 후 탈퇴")
        void withdrawalSuccess() {
            // Given: 로그인 상태인 유저 (로그인된 유저의 토큰 설정)
            String validToken = "valid-jwt-token";

            // 유저가 탈퇴 동의한 상태 설정
            when(jwtTokenProvider.getEmailFromToken(validToken)).thenReturn(testMember.getEmail()); // JWT에서 이메일 추출 mock
            when(memberRepository.findByEmail(testMember.getEmail())).thenReturn(Optional.of(testMember)); // 이메일로 멤버 조회 시 testMember 리턴
            when(jwtTokenProvider.isValidToken(validToken)).thenReturn(true);

            // 게시글 및 댓글의 작성자가 '탈퇴한 사용자'로 대체되도록 설정 (예시로 mock)
            // when(postRepository.updateAuthorToDeletedUser(testMember.getId())).thenReturn(1);  // 예시
            // when(commentRepository.updateAuthorToDeletedUser(testMember.getId())).thenReturn(1);  // 예시

            // When: 탈퇴 요청을 보내면
            //존재하지 않는 사용자입니다.
            //apptive.devlog.Global.Exception.InvalidRequestException: 존재하지 않는 사용자입니다. 에러 발생함 - 픽스필요

            boolean withdrawalResult = authServiceImpl.withdrawal(validToken);

            // Then: 계정 삭제, 게시글/댓글 작성자 변경 및 토큰 삭제
            // verify(postRepository).updateAuthorToDeletedUser(testMember.getId()); // 예시로 verify 설정
            // verify(commentRepository).updateAuthorToDeletedUser(testMember.getId()); // 예시로 verify 설정
            verify(jwtTokenProvider).invalidateToken(validToken);  // 토큰 무효화

            // 실제 탈퇴 성공 여부 확인
            assertTrue(withdrawalResult);  // 탈퇴 성공 여부 확인
        }


        @Test
        @DisplayName("탈퇴 성공 - 탈퇴 후 로그아웃 및 메인 페이지 리디렉션")
        void withdrawalRedirectsAfterLogout() {
            // Given: 로그인된 유저
            String validToken = "valid-jwt-token";

            when(jwtTokenProvider.isValidToken(validToken)).thenReturn(true);
            when(jwtTokenProvider.getEmailFromToken(validToken)).thenReturn(testMember.getEmail()); // JWT에서 이메일 추출 mock
            when(memberRepository.findByEmail(testMember.getEmail())).thenReturn(Optional.of(testMember)); // 이메일로 멤버 조회 시 testMember 리턴

            // When: 탈퇴 후 로그아웃 처리
            boolean withdrawalResult = authServiceImpl.withdrawal(validToken);

            // Then: 탈퇴 후 메인 페이지로 리디렉션 확인
            verify(jwtTokenProvider).invalidateToken(validToken);  // 토큰 무효화가 호출되었는지 확인
            assertTrue(withdrawalResult);  // 탈퇴 성공 후 로그아웃
        }


        @Test
        @DisplayName("탈퇴 실패 - 유효하지않은 토큰")
        void withdrawalFail_InvalidToken() {
            // Given: 로그인하지 않은 상태
            String invalidToken = "invalid-jwt-token";

            // When: 탈퇴 요청을 보내면
            when(jwtTokenProvider.isValidToken(invalidToken)).thenReturn(false);  // 로그인 상태가 아님

            // Then: 403 Forbidden 예외 발생
            assertThrows(InvalidTokenException.class, () -> authServiceImpl.withdrawal(invalidToken));  // 403 오류 발생
        }

        @Test
        @DisplayName("탈퇴 실패 - 유효하지않은 토큰")
        void withdrawalFail_MemberNotExist() {
            // Given: 로그인하지 않은 상태
            String invalidToken = "invalid-jwt-token";

            // When: 탈퇴 요청을 보내면
            when(jwtTokenProvider.isValidToken(invalidToken)).thenReturn(true);  // 로그인 상태로 가정

            // Then: 403 Forbidden 예외 발생
            assertThrows(MemberNotExistException.class, () -> authServiceImpl.withdrawal(invalidToken));  // 403 오류 발생
        }
    }
}

