package apptive.devlog.Member.Service;

import apptive.devlog.Global.Auth.Dto.SignUpDto;
import apptive.devlog.Global.Auth.Jwt.JwtTokenProvider;
import apptive.devlog.Global.Auth.Service.AuthService;
import apptive.devlog.Global.Auth.Validator.PasswordValidator;
import apptive.devlog.Member.Enum.Gender;
import apptive.devlog.Global.Exception.EmailDuplicationException;
import apptive.devlog.Global.Exception.InvalidTokenException;
import apptive.devlog.Global.Exception.MemberNotExistException;
import apptive.devlog.Global.Exception.NicknameDuplicationException;
import apptive.devlog.Global.Response.Error.ErrorCode;
import apptive.devlog.Member.Domain.Member;
import apptive.devlog.Member.Dto.UpdateProfileDto;
import apptive.devlog.Member.Repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@DisplayName("회원 테스트")
class MemberServiceTest {

    @Mock
    private PasswordValidator passwordValidator;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private AuthService authService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    private UpdateProfileDto updateProfileDto;

    private SignUpDto signUpDto;

    @InjectMocks
    private MemberServiceImpl memberServiceImpl;


    private final String email = "test@example.com";
    private final String pwd = "signupTest1#";
    private final String name = "테스트";

    private final String nickname = "테스트닉네임";
    private final String birth = "2025-03-29";

    private final Gender gender = Gender.MALE;

    @Nested
    @DisplayName("회원가입 테스트")
    class SignUp {

        @BeforeEach
        void setUp() {
            MockitoAnnotations.openMocks(this);
        }

        Member newMember;

        @Test
        @DisplayName("회원가입 성공 - 모든 조건 만족 시 회원가입 성공")
        void signUpSuccess() {
            //Given
            signUpDto = new SignUpDto(email, pwd, name, nickname, birth, gender);
            String encodePwd = passwordEncoder.encode(pwd);

            //When
            when(memberRepository.findByEmail(anyString())).thenReturn(Optional.empty()); // 이메일 중복 체크
            when(passwordValidator.isValid(pwd)).thenReturn(true); // 비밀번호 유효성 검사
            when(memberRepository.findByNickname(nickname)).thenReturn(Optional.empty()); // 닉네임 중복 체크

            newMember = new Member(email, name, nickname, LocalDate.parse(birth), gender,encodePwd);
            when(memberRepository.save(newMember)).thenReturn(newMember);
            //회원가입 시도
            Member resultMember = memberServiceImpl.signUp(signUpDto);

            //Then
            //DB에 등록된 멤버와 새로 저장된 멤버가 같아야함
            assertEquals(newMember, resultMember);
        }

        @Test
        @DisplayName("회원가입 실패 - 하나의 항목이라도 입력되지 않은 경우")
        void signUpFail_NullInput() {
            //Given
            signUpDto = new SignUpDto(null, pwd, name, nickname, birth, gender); // 이메일을 null로 설정

            //When
            //Then
            assertThrows(IllegalArgumentException.class, () -> memberServiceImpl.signUp(signUpDto));
        }

        @Test
        @DisplayName("회원가입 실패 - 이미 가입된 이메일의 경우")
        void signUpFail_EmailDup() {
            //Given
            signUpDto = new SignUpDto(email, pwd, name, nickname, birth, gender);
            String encodePwd = passwordEncoder.encode(pwd);
            newMember = new Member(email, name, nickname, LocalDate.parse(birth), gender,encodePwd);

            //When
            when(memberRepository.findByEmail(anyString())).thenReturn(Optional.of(newMember)); // 이미 가입된 이메일이 존재

            //Then
            assertThrows(EmailDuplicationException.class, () -> memberServiceImpl.signUp(signUpDto));
        }

        @Test
        @DisplayName("회원가입 실패 - pw가 조건에 만족하지 않는 경우")
        void signUpFail_InvalidPw() {
            //Given
            signUpDto = new SignUpDto(email, "invalidPassword", name, nickname, birth, gender);

            //When
            when(memberRepository.findByEmail(anyString())).thenReturn(Optional.empty());
            when(passwordValidator.isValid(anyString())).thenReturn(false); // 비밀번호가 조건에 맞지 않음

            //Then
            assertThrows(IllegalArgumentException.class, () -> memberServiceImpl.signUp(signUpDto));
        }

        @Test
        @DisplayName("회원가입 실패 - 닉네임이 중복되는 경우")
        void signUpFail_NicknameDup() {
            //Given
            signUpDto = new SignUpDto(email, pwd, name, nickname, birth, gender);
            String encodePwd = passwordEncoder.encode(pwd);
            newMember = new Member(email, name, nickname, LocalDate.parse(birth), gender,encodePwd);

            //When
            when(memberRepository.findByEmail(anyString())).thenReturn(Optional.empty());  // 이메일은 중복이 아니어야 하므로 빈 Optional 반환
            when(passwordValidator.isValid(anyString())).thenReturn(true);
            when(memberRepository.findByNickname(anyString())).thenReturn(Optional.of(newMember)); // 이미 존재하는 닉네임

            //Then
            assertThrows(NicknameDuplicationException.class, () -> memberServiceImpl.signUp(signUpDto));
        }

        @Test
        @DisplayName("회원가입 실패 - 생년월일이 숫자가 아닌 경우")
        void signUpFail_InValidBirth() {
            //Given
            signUpDto = new SignUpDto(email, pwd, name, nickname, "invalidBirth", gender);

            //When
            when(memberRepository.findByEmail(anyString())).thenReturn(Optional.empty());  // 이메일은 중복이 아니어야 하므로 빈 Optional 반환
            when(passwordValidator.isValid(anyString())).thenReturn(true);
            when(memberRepository.findByNickname(anyString())).thenReturn(Optional.empty());
            //Then
            assertThrows(IllegalArgumentException.class, () -> memberServiceImpl.signUp(signUpDto));
        }
    }

    @Nested
    @DisplayName("정보수정 테스트")
    @ExtendWith(MockitoExtension.class)
    class ModifyProfileTest {
        private final String validToken = "valid-jwt-token";
        private Member testMember;

        @BeforeEach
        void setUp() {
            MockitoAnnotations.openMocks(this);
            testMember = Member.builder()
                    .email("test@example.com")
                    .nickname("originalNick")
                    .password("EncodedPw1!")
                    .build();
        }


        @Test
        @DisplayName("성공 케이스 - 닉네임, 비밀번호 변경")
        void updateProfileSuccess() throws AccessDeniedException {
            updateProfileDto = new UpdateProfileDto("newNick", "StrongPw1!",validToken);

            when(jwtTokenProvider.isValidToken(validToken)).thenReturn(true);
            when(authService.getCurrentMember(validToken)).thenReturn(Optional.of(testMember));
            when(memberRepository.findByNickname("newNick")).thenReturn(Optional.empty());

            memberServiceImpl.updateProfile(updateProfileDto);

            assertEquals("newNick", testMember.getNickname());
            // 비밀번호는 인코딩 후 저장되므로 예시용 체크
            assertNotEquals("StrongPw1!", testMember.getPassword());
            verify(memberRepository).save(testMember);
        }

        @Test
        @DisplayName("유효하지 않은 토큰 - 실패")
        void updateProfileFail_InvalidToken() {
            updateProfileDto = new UpdateProfileDto("newNick", "StrongPw1!","invalid token");

            InvalidTokenException exception = assertThrows(
                    InvalidTokenException.class,
                    () -> memberServiceImpl.updateProfile(updateProfileDto)
            );

            assertEquals(ErrorCode.INVALID_TOKEN.getMessage(), exception.getMessage());
            verify(memberRepository, never()).save(any());
        }

        @Test
        @DisplayName("없는 사용자 - 실패")
        void updateProfileFail_NoUser() {
            updateProfileDto = new UpdateProfileDto("newNick", "StrongPw1!",validToken);


            when(jwtTokenProvider.isValidToken(anyString())).thenReturn(true);
            when(authService.getCurrentMember(validToken)).thenReturn(Optional.empty());

            MemberNotExistException exception = assertThrows(
                    MemberNotExistException.class,
                    () -> memberServiceImpl.updateProfile(updateProfileDto)
            );

            assertEquals("존재하지 않는 유저입니다.", exception.getMessage());
            verify(memberRepository, never()).save(any());
        }

        @Test
        @DisplayName("정보수정 실패 - 공백 정보로 수정 시도")
        void updateProfileFail_BlankFields() {
            updateProfileDto = new UpdateProfileDto("   ", "   ",validToken);


            when(jwtTokenProvider.isValidToken(anyString())).thenReturn(true);
            when(authService.getCurrentMember(validToken)).thenReturn(Optional.of(testMember));

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> memberServiceImpl.updateProfile(updateProfileDto)
            );

            assertEquals("수정된 정보는 공백일 수 없습니다.", exception.getMessage());
            verify(memberRepository, never()).save(any());
        }

        @Test
        @DisplayName("정보수정 실패 - 중복된 닉네임으로 수정 시도")
        void updateProfileWithDuplicateNickname() {
            updateProfileDto = new UpdateProfileDto("existingNick", "StrongPw1!",validToken);
            Member existMember = new Member();

            when(jwtTokenProvider.isValidToken(anyString())).thenReturn(true);
            when(authService.getCurrentMember(validToken)).thenReturn(Optional.of(testMember));
            when(memberRepository.findByNickname("existingNick")).thenReturn(Optional.of(existMember));

            NicknameDuplicationException exception = assertThrows(
                    NicknameDuplicationException.class,
                    () -> memberServiceImpl.updateProfile(updateProfileDto)
            );
            assertEquals("이미 사용중인 닉네임입니다.", exception.getMessage());
            verify(memberRepository, times(1)).findByNickname("existingNick");
            verify(memberRepository, never()).save(any());
        }

        @Test
        @DisplayName("정보수정 실패 - 비밀번호가 10자 미만")
        void updateProfileWithShortPassword() {
            updateProfileDto = new UpdateProfileDto("newNick", "aB1!x",validToken); // 6자

            when(jwtTokenProvider.isValidToken(anyString())).thenReturn(true);
            when(authService.getCurrentMember(validToken)).thenReturn(Optional.of(testMember));
            when(memberRepository.findByNickname("newNick")).thenReturn(Optional.empty());

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> memberServiceImpl.updateProfile(updateProfileDto)
            );

            assertEquals("비밀번호는 10자 이상이어야 합니다.", exception.getMessage());
            verify(memberRepository, never()).save(any());
        }

        @Test
        @DisplayName("비밀번호에 대소문자, 특수문자가 포함되지 않음 - 실패")
        void updateProfileWithWeakPassword() {
            updateProfileDto = new UpdateProfileDto("newNick", "password123",validToken);


            when(jwtTokenProvider.isValidToken(anyString())).thenReturn(true);
            when(authService.getCurrentMember(validToken)).thenReturn(Optional.of(testMember));
            when(memberRepository.findByNickname("newNick")).thenReturn(Optional.empty());

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> memberServiceImpl.updateProfile(updateProfileDto)
            );

            assertEquals("비밀번호는 대소문자와 특수문자를 포함해야 합니다.", exception.getMessage());
            verify(memberRepository, never()).save(any());
        }
    }
}
