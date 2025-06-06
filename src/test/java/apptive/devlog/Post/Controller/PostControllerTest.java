package apptive.devlog.Post.Controller;

import apptive.devlog.Global.Exception.PostContentBlankException;
import apptive.devlog.Global.Exception.PostNoPermissionException;
import apptive.devlog.Global.Exception.PostNotFoundException;
import apptive.devlog.Global.Exception.PostTitleBlankException;
import apptive.devlog.Member.Domain.Member;
import apptive.devlog.Member.Enum.Gender;
import apptive.devlog.Post.Domain.Post;
import apptive.devlog.Post.Dto.CreatePostRequest;
import apptive.devlog.Post.Dto.UpdatePostRequest;
import apptive.devlog.Post.Service.PostService;
import apptive.devlog.Post.Service.PostServiceImpl;
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
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WithMockUser
@ExtendWith(MockitoExtension.class)
@WebMvcTest(PostController.class)
@MockitoBean(name = "postService", types = PostServiceImpl.class)
@ActiveProfiles("test")
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PostService postService;

    private CreatePostRequest createPostRequest;
    private UpdatePostRequest updatePostRequest;
    private Member testMember;

    private Post post;

    @BeforeEach
    void setUp() {
        createPostRequest = new CreatePostRequest(
            "테스트 제목",
            "테스트 내용"
        );

        updatePostRequest = new UpdatePostRequest(
            "수정된 제목",
            "수정된 내용"
        );

        testMember = new Member(
            "test@example.com",
            "Test User",
            "TestUser",
            LocalDate.now(),
            Gender.MALE,
            "password123"
        );

        post = new Post();
        post.setId(1L);
        post.setTitle("테스트 제목");
        post.setContent("테스트 내용");
        post.setAuthor(testMember);
    }

    @Nested
    @DisplayName("게시글 작성 테스트")
    class CreatePost {
        @Test
        @DisplayName("게시글 작성 성공 테스트")
        void createPostSuccess() throws Exception {
            // When
            when(postService.createPost(any(CreatePostRequest.class), eq(testMember)))
                .thenReturn(1L);

            // Then
            mockMvc.perform(post("/api/posts")
                    .with(csrf())
                    .with(authentication(
                            new UsernamePasswordAuthenticationToken(testMember, null, List.of(new SimpleGrantedAuthority("ROLE_USER")))
                    ))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createPostRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("게시글 작성에 성공하였습니다."))
                .andExpect(jsonPath("$.data.postId").value(1L));

            verify(postService).createPost(any(CreatePostRequest.class), any(Member.class));
        }

        @Test
        @DisplayName("게시글 작성 실패 테스트 - 제목 공백")
        void createPostFail_BlankTitle() throws Exception {
            // Given
            CreatePostRequest blankTitleRequest = new CreatePostRequest("", "테스트 내용");

            // When
            when(postService.createPost(any(CreatePostRequest.class), nullable(Member.class)))
                    .thenThrow(new PostTitleBlankException());

            // Then
            mockMvc.perform(post("/api/posts")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(blankTitleRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("제목은 공백일 수 없습니다."));
        }

        @Test
        @DisplayName("게시글 작성 실패 테스트 - 내용 공백")
        void createPostFail_BlankContent() throws Exception {
            // Given
            CreatePostRequest blankContentRequest = new CreatePostRequest("테스트 제목", "");

            // When
            when(postService.createPost(any(CreatePostRequest.class), nullable(Member.class)))
                    .thenThrow(new PostContentBlankException());


            // Then
            mockMvc.perform(post("/api/posts")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(blankContentRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("내용은 공백일 수 없습니다."));
        }
    }

    @Nested
    @DisplayName("게시글 조회 테스트")
    class ReadPost {
        @Test
        @DisplayName("게시글 조회 성공 테스트")
        void readPostSuccess() throws Exception {
            // Given
            Long postId = 1L;
            when(postService.getOnePost(postId)).thenReturn(post);


            // When & Then
            mockMvc.perform(get("/api/posts/{postId}", postId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").exists())
                .andExpect(jsonPath("$.data.content").exists());
        }

        @Test
        @DisplayName("게시글 조회 실패 테스트 - 존재하지 않는 게시글")
        void readPostFail_NotExist() throws Exception {
            // Given
            Long postId = 999L;

            // When
            when(postService.getOnePost(postId))
                .thenThrow(new PostNotFoundException());

            // Then
            mockMvc.perform(get("/api/posts/{postId}", postId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("존재하지 않는 게시글입니다."));
        }
    }

    @Nested
    @DisplayName("게시글 수정 테스트")
    class UpdatePost {
        @Test
        @DisplayName("게시글 수정 성공 테스트")
        void updatePostSuccess() throws Exception {
            // Given
            Long postId = 1L;

            // When & Then
            mockMvc.perform(put("/api/posts/{postId}", postId)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updatePostRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("게시글을 성공적으로 수정하였습니다."));
        }

        @Test
        @DisplayName("게시글 수정 실패 테스트 - 권한 없음")
        void updatePostFail_NoPermission() throws Exception {
            // Given
            Long postId = 1L;

            // When
            doThrow(new PostNoPermissionException())
                    .when(postService).updatePost(anyLong(), any(UpdatePostRequest.class), nullable(Member.class));

            // Then
            mockMvc.perform(put("/api/posts/{postId}", postId)
                    .with(csrf())
                    .with(user("test@example.com")   // SecurityContext에 유저 정보 추가
                            .roles("USER")
                    ).contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updatePostRequest)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("게시글 접근 권한이 없습니다."));
        }
    }

    @Nested
    @DisplayName("게시글 삭제 테스트")
    class DeletePost {
        @Test
        @DisplayName("게시글 삭제 성공 테스트")
        void deletePostSuccess() throws Exception {
            // Given
            Long postId = 1L;
            doNothing().when(postService).deletePost(postId,testMember);

            // When & Then
            mockMvc.perform(delete("/api/posts/{postId}", postId)
                    .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("게시글을 성공적으로 삭제하였습니다."));
        }

        @Test
        @DisplayName("게시글 삭제 실패 테스트 - 권한 없음")
        void deletePostFail_NoPermission() throws Exception {
            // Given
            Long postId = 1L;

            // When
            doThrow(new PostNoPermissionException())
                    .when(postService).deletePost(anyLong(),nullable(Member.class));

            // Then
            mockMvc.perform(delete("/api/posts/{postId}", postId)
                    .with(csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("게시글 접근 권한이 없습니다."));
        }
    }
} 