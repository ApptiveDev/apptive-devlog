package apptive.devlog.Post.Service;

import apptive.devlog.Global.Exception.PostContentBlankException;
import apptive.devlog.Global.Exception.PostNoPermissionException;
import apptive.devlog.Global.Exception.PostNotFoundException;
import apptive.devlog.Global.Exception.PostTitleBlankException;
import apptive.devlog.Global.Response.Error.ErrorCode;
import apptive.devlog.Member.Domain.Member;
import apptive.devlog.Post.Domain.Post;
import apptive.devlog.Post.Dto.CreatePostRequest;
import apptive.devlog.Post.Dto.UpdatePostRequest;
import apptive.devlog.Post.Repository.PostRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @InjectMocks
    private PostServiceImpl postService;

    @Mock
    private PostRepository postRepository;

    private Member member;
    private Post post;
    private CreatePostRequest createPostRequest;
    private UpdatePostRequest updatePostRequest;

    @BeforeEach
    void setUp() {
        member = new Member();
        member.setEmail("test@example.com");
        member.setNickname("TestUser");

        post = new Post();
        post.setId(1L);
        post.setTitle("테스트 제목");
        post.setContent("테스트 내용");
        post.setAuthor(member);

        createPostRequest = new CreatePostRequest(
            "테스트 제목",
            "테스트 내용"
        );

        updatePostRequest = new UpdatePostRequest(
            "수정된 제목",
            "수정된 내용"
        );
    }

    @Nested
    @DisplayName("게시글 작성 테스트")
    class CreatePost {
        @Test
        @DisplayName("게시글 작성 성공 테스트")
        void createPostSuccess() {
            // Given
            when(postRepository.save(any(Post.class))).thenReturn(post);

            // When
            Long postId = postService.createPost(createPostRequest, member);

            // Then
            assertThat(postId).isEqualTo(1L);
            verify(postRepository, times(1)).save(any(Post.class));
        }

        @Test
        @DisplayName("게시글 작성 실패 테스트 - 제목 공백")
        void createPostFail_BlankTitle() {
            // Given
            createPostRequest.setTitle("");

            // When & Then
            assertThatThrownBy(() -> postService.createPost(createPostRequest, member))
                .isInstanceOf(PostTitleBlankException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.POST_TITLE_BLANK);
        }

        @Test
        @DisplayName("게시글 작성 실패 테스트 - 내용 공백")
        void createPostFail_BlankContent() {
            // Given
            createPostRequest.setContent("");

            // When & Then
            assertThatThrownBy(() -> postService.createPost(createPostRequest, member))
                .isInstanceOf(PostContentBlankException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.POST_CONTENT_BLANK);
        }
    }

    @Nested
    @DisplayName("게시글 조회 테스트")
    class ReadPost {
        @Test
        @DisplayName("게시글 조회 성공 테스트")
        void readPostSuccess() {
            // Given
            when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));

            // When
            Post foundPost = postService.getOnePost(1L);

            // Then
            assertThat(foundPost).isNotNull();
            assertThat(foundPost.getTitle()).isEqualTo("테스트 제목");
            assertThat(foundPost.getContent()).isEqualTo("테스트 내용");
        }

        @Test
        @DisplayName("게시글 조회 실패 테스트 - 존재하지 않는 게시글")
        void readPostFail_NotExist() {
            // Given
            when(postRepository.findById(anyLong())).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> postService.getOnePost(999L))
                .isInstanceOf(PostNotFoundException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.POST_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("게시글 수정 테스트")
    class UpdatePost {
        @Test
        @DisplayName("게시글 수정 성공 테스트")
        void updatePostSuccess() {
            // Given
            when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
            when(postRepository.save(any(Post.class))).thenReturn(post);

            // When
            postService.updatePost(1L, updatePostRequest, member);

            // Then
            assertThat(post.getTitle()).isEqualTo("수정된 제목");
            assertThat(post.getContent()).isEqualTo("수정된 내용");
            verify(postRepository, times(1)).save(any(Post.class));
        }

        @Test
        @DisplayName("게시글 수정 실패 테스트 - 권한 없음")
        void updatePostFail_NoPermission() {
            // Given
            Member otherMember = new Member();
            otherMember.setEmail("other@example.com");
            otherMember.setNickname("OtherUser");
            when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));

            // When & Then
            assertThatThrownBy(() -> postService.updatePost(1L, updatePostRequest, otherMember))
                .isInstanceOf(PostNoPermissionException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.POST_NO_PERMISSION);
        }
    }

    @Nested
    @DisplayName("게시글 삭제 테스트")
    class DeletePost {
        @Test
        @DisplayName("게시글 삭제 성공 테스트")
        void deletePostSuccess() {
            // Given
            when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
            doNothing().when(postRepository).delete(any(Post.class));

            // When
            postService.deletePost(1L, member);

            // Then
            verify(postRepository, times(1)).delete(post);
        }

        @Test
        @DisplayName("게시글 삭제 실패 테스트 - 권한 없음")
        void deletePostFail_NoPermission() {
            // Given
            Member otherMember = new Member();
            otherMember.setEmail("other@example.com");
            otherMember.setNickname("OtherUser");
            when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));

            // When & Then
            assertThatThrownBy(() -> postService.deletePost(1L, otherMember))
                .isInstanceOf(PostNoPermissionException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.POST_NO_PERMISSION);
        }
    }
}