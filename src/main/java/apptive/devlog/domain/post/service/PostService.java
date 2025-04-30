package apptive.devlog.domain.post.service;

import apptive.devlog.domain.post.dto.PostDto;
import apptive.devlog.domain.post.entity.Post;
import apptive.devlog.domain.post.repository.PostRepository;
import apptive.devlog.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PostService {
    private final PostRepository postRepository;

    public PostDto.Response create(PostDto.Create dto, User author) {
        Post post = Post.builder().title(dto.title()).content(dto.content()).author(author).build();
        postRepository.save(post);
        return toResponse(post);
    }

    public PostDto.Response read(String postId) {
        return postRepository.findById(postId).map(this::toResponse).orElseThrow(() -> new IllegalArgumentException("Post not found"));
    }

    public PostDto.Response update(String postId, PostDto.Update dto, User user) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("Post not found"));
        if (!post.getAuthor().getId().equals(user.getId())) {
            throw new IllegalArgumentException("작성자만 수정할 수 있습니다.");
        }
        post.update(dto.title(), dto.content());
        return toResponse(post);
    }

    public void delete(String postId, User user) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("Post not found"));
        if (!post.getAuthor().getId().equals(user.getId())) {
            throw new IllegalArgumentException("작성자만 삭제할 수 있습니다.");
        }
        postRepository.delete(post);
    }

    private PostDto.Response toResponse(Post post) {
        return PostDto.Response.builder().id(post.getId()).title(post.getTitle()).content(post.getContent()).authorId(post.getAuthor().getId()).build();
    }
}
