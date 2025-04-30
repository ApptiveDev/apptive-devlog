package apptive.devlog.domain.comment.service;

import apptive.devlog.domain.comment.dto.CommentDto;
import apptive.devlog.domain.comment.entity.Comment;
import apptive.devlog.domain.comment.repository.CommentRepository;
import apptive.devlog.domain.post.entity.Post;
import apptive.devlog.domain.post.repository.PostRepository;
import apptive.devlog.domain.user.entity.User;
import apptive.devlog.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    private static final int MAX_DEPTH = 3;

    @Transactional
    public CommentDto.Response create(String postId, CommentDto.Create dto, User author) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("post not found"));
        User user = userRepository.findById(author.getId()).orElseThrow(() -> new IllegalArgumentException("user not found"));

        Comment parent = null;
        int depth = 0;
        if (dto.parentId() != null) {
            parent = commentRepository.findByIdAndDeletedFalse(dto.parentId()).orElseThrow(() -> new IllegalArgumentException("parent comment not found"));
            depth = calculateDepth(parent);
            if (depth >= MAX_DEPTH) {
                throw new IllegalArgumentException("댓글은 최대 %d단계까지만 가능합니다.".formatted(MAX_DEPTH));
            }
        }

        Comment comment = Comment.builder().text(dto.text()).author(author).parent(parent).post(post).build();
        commentRepository.save(comment);
        return toResponse(comment);
    }

    @Transactional(readOnly = true)
    public List<CommentDto.Response> read(String postId) {
        return commentRepository.findByPostIdAndParentIsNullOrderByCreatedAtDesc(postId).stream().map(this::toResponse).toList();
    }

    @Transactional
    public CommentDto.Response update(String commentId, CommentDto.Update dto, User user) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new IllegalArgumentException("comment not found"));
        if (!comment.getAuthor().getId().equals(user.getId())) {
            throw new IllegalArgumentException("수정 권한이 없습니다");
        }
        comment.updateText(dto.text());
        return toResponse(comment);
    }

    @Transactional
    public void delete(String commentId, User user) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new IllegalArgumentException("comment not found"));
        if (!comment.getAuthor().getId().equals(user.getId())) {
            throw new IllegalArgumentException("삭제 권한이 없습니다.");
        }
        commentRepository.delete(comment);
    }

    private int calculateDepth(Comment comment) {
        int depth = 0;
        while (comment.getParent() != null) {
            comment = comment.getParent();
            depth++;
        }
        return depth;
    }

    private CommentDto.Response toResponse(Comment comment) {
        List<CommentDto.Response> replies = comment.getReplies() == null ? List.of() : comment.getReplies().stream().map(this::toResponse).toList();
        return CommentDto.Response.builder().id(comment.getId()).text(comment.getText()).authorId(comment.getAuthor().getId()).parentId(comment.getParent() != null ? comment.getParent().getId() : null).replies(replies).build();
    }
}
