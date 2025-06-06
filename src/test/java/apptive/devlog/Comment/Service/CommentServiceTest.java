package apptive.devlog.Comment.Service;

import apptive.devlog.Global.Exception.CommentContentBlankException;
import apptive.devlog.Global.Exception.CommentNoPermissionException;
import apptive.devlog.Global.Exception.CommentNotFoundException;
import apptive.devlog.Global.Exception.PostNotFoundException;
import apptive.devlog.Member.Domain.Member;
import apptive.devlog.Post.Domain.Post;
import apptive.devlog.Comment.Domain.Comment;
import apptive.devlog.Comment.Dto.CreateCommentRequest;
import apptive.devlog.Comment.Repository.CommentRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @InjectMocks
    private CommentServiceImpl commentService;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostRepository postRepository;

    private Member member;
    private Post post;
    private Comment comment;
    private CreateCommentRequest createCommentRequest;

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

        comment = new Comment();
        comment.setId(1L);
        comment.setContent("테스트 댓글 내용");
        comment.setAuthor(member);
        comment.setPost(post);

        createCommentRequest = new CreateCommentRequest(
            "테스트 댓글 내용"
        );
    }

    @Nested
    @DisplayName("댓글 작성 테스트")
    class CreateComment {
        @Test
        @DisplayName("댓글 작성 성공 테스트")
        void createCommentSuccess() {
            // Given
            when(postRepository.findById(1L)).thenReturn(Optional.of(post));
            when(commentRepository.save(any(Comment.class))).thenReturn(comment);

            // When
            Long commentId = commentService.createComment(1L, createCommentRequest, member);

            // Then
            assertThat(commentId).isEqualTo(1L);
            verify(commentRepository, times(1)).save(any(Comment.class));
        }

        @Test
        @DisplayName("댓글 작성 실패 테스트 - 내용 공백")
        void createCommentFail_BlankContent() {
            // Given
            createCommentRequest.setContent("");

            // When & Then
            assertThatThrownBy(() -> commentService.createComment(1L, createCommentRequest, member))
                .isInstanceOf(CommentContentBlankException.class)
                .hasMessage("댓글 내용은 공백일 수 없습니다.");
        }

        @Test
        @DisplayName("댓글 작성 실패 테스트 - 존재하지 않는 게시글")
        void createCommentFail_NotExistPost() {
            // Given
            when(postRepository.findById(999L)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> commentService.createComment(999L, createCommentRequest, member))
                .isInstanceOf(PostNotFoundException.class)
                .hasMessage("존재하지 않는 게시글입니다.");
        }
    }

    @Nested
    @DisplayName("댓글 삭제 테스트")
    class DeleteComment {
        @Test
        @DisplayName("댓글 삭제 성공 테스트")
        void deleteCommentSuccess() {
            // Given
            when(postRepository.findById(1L)).thenReturn(Optional.of(post));
            when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));

            // When
            commentService.deleteComment(1L, 1L, member);

            // Then
            verify(commentRepository, times(1)).delete(comment);
        }

        @Test
        @DisplayName("댓글 삭제 실패 테스트 - 권한 없음")
        void deleteCommentFail_NoPermission() {
            // Given
            Member otherMember = new Member();
            otherMember.setEmail("other@example.com");
            otherMember.setNickname("OtherUser");
            when(postRepository.findById(1L)).thenReturn(Optional.of(post));
            when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));

            // When & Then
            assertThatThrownBy(() -> commentService.deleteComment(1L, 1L, otherMember))
                .isInstanceOf(CommentNoPermissionException.class)
                .hasMessage("댓글 접근 권한이 없습니다.");
        }

        @Test
        @DisplayName("댓글 삭제 실패 테스트 - 존재하지 않는 댓글")
        void deleteCommentFail_NotExist() {
            // Given
            when(postRepository.findById(1L)).thenReturn(Optional.of(post));
            when(commentRepository.findById(999L)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> commentService.deleteComment(1L, 999L, member))
                .isInstanceOf(CommentNotFoundException.class)
                .hasMessage("존재하지 않는 댓글입니다.");
        }
    }
}