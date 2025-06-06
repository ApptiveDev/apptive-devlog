package apptive.devlog.Member.Controller;

import apptive.devlog.Global.Auth.Dto.SignUpDto;
import apptive.devlog.Member.Enum.Gender;
import apptive.devlog.Global.Exception.EmailDuplicationException;
import apptive.devlog.Global.Exception.InvalidTokenException;
import apptive.devlog.Global.Exception.MemberNotExistException;
import apptive.devlog.Global.Exception.NicknameDuplicationException;
import apptive.devlog.Global.Response.Error.ErrorCode;
import apptive.devlog.Global.Response.Result.ResultCode;
import apptive.devlog.Member.Domain.Member;
import apptive.devlog.Member.Dto.UpdateProfileDto;
import apptive.devlog.Member.Dto.UpdateProfileRequest;
import apptive.devlog.Member.Service.MemberService;
import apptive.devlog.Member.Service.MemberServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;

@WithMockUser // 테스트에서 인증된 사용자로 실행!
@ExtendWith(MockitoExtension.class) // Mockito 확장 활성화
@WebMvcTest(MemberController.class)   // 컨트롤러만 테스트
@MockitoBean(name = "memberService",types = MemberServiceImpl.class)
@ActiveProfiles("test")
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private MemberService memberService;

    private SignUpDto signUpDto;


    @Nested
    @DisplayName("회원가입 테스트")
    class Signup{
        @BeforeEach
        void setUp() {

            signUpDto = new SignUpDto(
                    "test@example.com",
                    "StrongPassword1!",
                    "TestName",
                    "TestNick",
                    "2025-03-30",
                    Gender.MALE
            );
        }


        @Test
        @DisplayName("회원가입 성공 테스트")
        void signupSuccess() throws Exception {
            // Given
            // 실제 SignUpDto 생성 (필드 순서와 타입은 실제 클래스에 맞춰 조정)

            // 회원가입 후 반환될 Member 객체 생성
            Member member = new Member();
            member.setEmail("test@example.com");
            member.setNickname("TestNick");
            // 나머지 필드도 필요 시 설정 (예: 이름, 생년월일, 성별, 비밀번호 등)
            member.setPassword("EncodedPassword1!"); // 실제 인코딩된 비밀번호

            // When: 회원가입 시 MemberService.signUp() 호출 시 member 반환하도록 설정
            when(memberService.signUp(any(SignUpDto.class))).thenReturn(member);

            // Then: API 호출 및 검증
            mockMvc.perform(post("/members/signup")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(signUpDto)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.message").value(ResultCode.REGISTER_SUCCESS.getMessage()))
                    .andExpect(jsonPath("$.data.email").value("test@example.com"));
        }

        @Test
        @DisplayName("회원가입 실패 테스트-입력오류")
        void loginFail_InvalidInput() throws Exception {
            // Given
            // When
            when(memberService.signUp(any(SignUpDto.class))).thenThrow(new IllegalArgumentException("입력값 누락"));

            // Then
            mockMvc.perform(post("/members/signup")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(signUpDto)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.errors[0].field").value("입력값 누락"));
        }


        @Test
        @DisplayName("회원가입 실패 테스트-아이디 중복")
        void loginFail_ExistId() throws Exception {
            // Given
            // When
            when(memberService.signUp(any(SignUpDto.class))).thenThrow(new EmailDuplicationException());

            // Then
            mockMvc.perform(post("/members/signup")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(signUpDto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value(ErrorCode.EMAIL_DUPLICATION.getMessage()));
        }

        @Test
        @DisplayName("회원가입 실패 테스트-닉네임 중복")
        void loginFail_ExistNickname() throws Exception {
            // Given
            // When
            when(memberService.signUp(any(SignUpDto.class))).thenThrow(new NicknameDuplicationException());

            // Then
            mockMvc.perform(post("/members/signup")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(signUpDto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value(ErrorCode.NICKNAME_DUPLICATION.getMessage()));
        }

        @Test
        @DisplayName("회원가입 실패 테스트-pw 조건")
        void loginFail_InvalidPw() throws Exception {
            // Given
            // When
            when(memberService.signUp(any(SignUpDto.class))).thenThrow(new IllegalArgumentException("비밀번호가 유효하지 않습니다."));

            // Then
            mockMvc.perform(post("/members/signup")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(signUpDto)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.errors[0].field").value("비밀번호가 유효하지 않습니다."));
        }

        @Test
        @DisplayName("회원가입 실패 테스트-생년월일 파싱오류")
        void loginFail_Birth() throws Exception {
            // Given
            // When
            when(memberService.signUp(any(SignUpDto.class))).thenThrow(new IllegalArgumentException("Invalid date format"));

            // Then
            mockMvc.perform(post("/members/signup")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(signUpDto)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.errors[0].field").value("Invalid date format"));
        }
    }

    @Nested
    @DisplayName("정보수정 테스트")
    class ModifyProfile{

        @Test
        @DisplayName("회원 정보 수정 성공 테스트")
        void updateProfileSuccess() throws Exception {
            // Given
            // UpdateProfileRequest 생성 (닉네임과 비밀번호 변경)
            UpdateProfileRequest updateProfileRequest = new UpdateProfileRequest("NewNick", "NewStrongPassword1!");
            // 유효한 토큰 예시 (헤더에 "Bearer " 접두어 포함)
            String token = "Bearer validToken";

            // updateProfile 메서드는 void를 리턴하므로, memberService.updateProfile()가 정상 동작(예외 없음)하도록 설정
            doNothing().when(memberService).updateProfile(any(UpdateProfileDto.class));

            // Then: API 호출 및 검증 (컨트롤러는 성공 시 ResultResponse를 반환)
            mockMvc.perform(patch("/members/update")
                            .with(csrf())
                            .header("Authorization", token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateProfileRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value(ResultCode.UPDATE_SUCCESS.getMessage()));
        }

        @Test
        @DisplayName("정보수정 실패 테스트-잘못된 토큰")
        void updateFail_InvalidToken() throws Exception {
            // Given
            // UpdateProfileRequest 생성 (닉네임과 비밀번호 변경)
            UpdateProfileRequest updateProfileRequest = new UpdateProfileRequest("NewNick", "NewStrongPassword1!");
            // 유효한 토큰 예시 (헤더에 "Bearer " 접두어 포함)
            String token = "Bearer validToken";

            doThrow(new InvalidTokenException())
                    .when(memberService).updateProfile(any(UpdateProfileDto.class));

            mockMvc.perform(patch("/members/update")
                            .with(csrf())
                            .header("Authorization", token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateProfileRequest)))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.errors[0].field").value(ErrorCode.INVALID_TOKEN.getMessage()));
        }

        @Test
        @DisplayName("정보수정 실패 테스트-없는 사용자")
        void updateFail_InvalidUser() throws Exception {
            // Given
            // UpdateProfileRequest 생성 (닉네임과 비밀번호 변경)
            UpdateProfileRequest updateProfileRequest = new UpdateProfileRequest("NewNick", "NewStrongPassword1!");
            // 유효한 토큰 예시 (헤더에 "Bearer " 접두어 포함)
            String token = "Bearer validToken";

            doThrow(new MemberNotExistException())
                    .when(memberService).updateProfile(any(UpdateProfileDto.class));

            mockMvc.perform(patch("/members/update")
                            .with(csrf())
                            .header("Authorization", token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateProfileRequest)))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.errors[0].field").value(ErrorCode.MEMBER_NOT_EXIST.getMessage()));
        }

        @Test
        @DisplayName("정보수정 실패 테스트-수정 정보 공백")
        void updateFail_Blank() throws Exception {
            // Given
            // UpdateProfileRequest 생성 (닉네임과 비밀번호 변경)
            UpdateProfileRequest updateProfileRequest = new UpdateProfileRequest(" ", "");
            // 유효한 토큰 예시 (헤더에 "Bearer " 접두어 포함)
            String token = "Bearer validToken";
            doThrow(new IllegalArgumentException("수정된 정보는 공백일 수 없습니다."))
                    .when(memberService).updateProfile(any(UpdateProfileDto.class));

            mockMvc.perform(patch("/members/update")
                            .with(csrf())
                            .header("Authorization", token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateProfileRequest)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.errors[0].field").value("수정된 정보는 공백일 수 없습니다."));
        }
    }


}

