package apptive.devlog.Comment.Controller;

import apptive.devlog.Global.Exception.CommentContentBlankException;
import apptive.devlog.Global.Exception.CommentNoPermissionException;
import apptive.devlog.Member.Domain.Member;
import apptive.devlog.Member.Enum.Gender;
import apptive.devlog.Comment.Dto.CreateCommentRequest;
import apptive.devlog.Comment.Service.CommentService;
import apptive.devlog.Comment.Service.CommentServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WithMockUser
@ExtendWith(MockitoExtension.class)
@WebMvcTest(CommentController.class)
@MockitoBean(name = "commentService", types = CommentServiceImpl.class)
@ActiveProfiles("test")
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CommentService commentService;

    private CreateCommentRequest createCommentRequest;
    private Member testMember;

    @BeforeEach
    void setUp() {
        createCommentRequest = new CreateCommentRequest(
            "테스트 댓글 내용"
        );

        testMember = new Member(
            "test@example.com",
            "Test User",
            "TestUser",
            LocalDate.now(),
            Gender.MALE,
            "password123"
        );
    }

    @Nested
    @DisplayName("댓글 작성 테스트")
    class CreateComment {
        @Test
        @DisplayName("최상위 댓글 작성 성공 테스트")
        void createRootCommentSuccess() throws Exception {
            // Given
            Long postId = 1L;

            // When
            when(commentService.createComment(any(Long.class), any(CreateCommentRequest.class), any(Member.class)))
                .thenReturn(1L);

            // Then
            mockMvc.perform(post("/api/posts/{postId}/comments", postId)
                    .with(csrf())
                    .with(authentication(
                            new UsernamePasswordAuthenticationToken(testMember, null, List.of(new SimpleGrantedAuthority("ROLE_USER")))
                    ))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createCommentRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("댓글 작성에 성공하였습니다."))
                .andExpect(jsonPath("$.data.commentId").value(1L));
        }

        @Test
        @DisplayName("대댓글 작성 성공 테스트")
        void createReplyCommentSuccess() throws Exception {
            // Given
            Long postId = 1L;
            Long parentCommentId = 1L;
            CreateCommentRequest replyRequest = new CreateCommentRequest("대댓글 내용", parentCommentId);

            // When
            when(commentService.createComment(any(Long.class), any(CreateCommentRequest.class), any(Member.class)))
                .thenReturn(2L);

            // Then
            mockMvc.perform(post("/api/posts/{postId}/comments", postId)
                    .with(csrf())
                    .with(authentication(
                            new UsernamePasswordAuthenticationToken(testMember, null, List.of(new SimpleGrantedAuthority("ROLE_USER")))
                    ))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(replyRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("댓글 작성에 성공하였습니다."))
                .andExpect(jsonPath("$.data.commentId").value(2L));
        }

        @Test
        @DisplayName("댓글 작성 실패 테스트 - 내용 공백")
        void createCommentFail_BlankContent() throws Exception {
            // Given
            Long postId = 1L;
            CreateCommentRequest blankRequest = new CreateCommentRequest("");
            when(commentService.createComment(any(Long.class), any(CreateCommentRequest.class), any(Member.class)))
                    .thenThrow(new CommentContentBlankException());

            // When & Then
            mockMvc.perform(post("/api/posts/{postId}/comments", postId)
                    .with(csrf())
                    .with(authentication(
                            new UsernamePasswordAuthenticationToken(testMember, null, List.of(new SimpleGrantedAuthority("ROLE_USER")))
                    ))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(blankRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("댓글 내용은 공백일 수 없습니다."));
        }
    }

    @Nested
    @DisplayName("댓글 삭제 테스트")
    class DeleteComment {
        @Test
        @DisplayName("댓글 삭제 성공 테스트")
        void deleteCommentSuccess() throws Exception {
            // Given
            Long postId = 1L;
            Long commentId = 1L;

            // When & Then
            mockMvc.perform(delete("/api/posts/{postId}/comments/{commentId}", postId, commentId)
                    .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("댓글을 성공적으로 삭제하였습니다."));
        }

        @Test
        @DisplayName("댓글 삭제 실패 테스트 - 권한 없음")
        void deleteCommentFail_NoPermission() throws Exception {
            // Given
            Long postId = 1L;
            Long commentId = 1L;

            // When
            doThrow(new CommentNoPermissionException())
                    .when(commentService).deleteComment(anyLong(), anyLong(), any(Member.class));

            // Then
            mockMvc.perform(delete("/api/posts/{postId}/comments/{commentId}", postId, commentId)
                .with(csrf())
                .with(authentication(
                        new UsernamePasswordAuthenticationToken(testMember, null, List.of(new SimpleGrantedAuthority("ROLE_USER")))
                )))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("댓글 접근 권한이 없습니다."));
        }
    }
} 