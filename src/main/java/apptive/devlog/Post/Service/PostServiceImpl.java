package apptive.devlog.Post.Service;

import apptive.devlog.Global.Exception.PostContentBlankException;
import apptive.devlog.Global.Exception.PostNoPermissionException;
import apptive.devlog.Global.Exception.PostNotFoundException;
import apptive.devlog.Global.Exception.PostTitleBlankException;
import apptive.devlog.Member.Domain.Member;
import apptive.devlog.Member.Repository.MemberRepository;
import apptive.devlog.Post.Domain.Post;
import apptive.devlog.Post.Dto.CreatePostRequest;
import apptive.devlog.Post.Dto.UpdatePostRequest;
import apptive.devlog.Post.Repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public Long createPost(CreatePostRequest request, Member member) {
        validateCreatePostRequest(request);

        Post post = new Post();
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setAuthor(member);

        Post savedPost = postRepository.save(post);
        return savedPost.getId();
    }

    private void validateCreatePostRequest(CreatePostRequest request) {
        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            throw new PostTitleBlankException();
        }
        if (request.getContent() == null || request.getContent().trim().isEmpty()) {
            throw new PostContentBlankException();
        }
    }

    private static <T> List<T> requireNonEmpty(List<T> list) {
        return Optional.ofNullable(list)
                .filter(l -> !l.isEmpty())
                .orElseThrow(PostNotFoundException::new);
    }


    @Override
    public Post getOnePost(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(PostNotFoundException::new);
    }

    @Override
    public List<Post> getPostsByTitle(String title) {
        return requireNonEmpty(postRepository.findByTitleContaining(title));

    }

    @Override
    public List<Post> getPostsByContent(String content) {
        return requireNonEmpty(postRepository.findByContentContaining(content));

    }

    @Override
    public List<Post> getMyPosts(Member author) {
        return requireNonEmpty(postRepository.findByAuthor(Optional.ofNullable(author)));

    }

    @Override
    public List<Post> getPostsByAuthor(String nickname) {
        Optional<Member> author = memberRepository.findByNickname(nickname);
        return requireNonEmpty(postRepository.findByAuthor(author));

    }
    @Override
    public List<Post> getPostsByTitleAndContent(String title, String content) {
        return requireNonEmpty(postRepository.findByTitleContainingAndContentContaining(title,content));

    }

    @Override
    @Transactional
    public void updatePost(Long postId, UpdatePostRequest request, Member member) {
        Post post = getOnePost(postId);
        
        if (!post.getAuthor().equals(member)) {
            throw new PostNoPermissionException();
        }
        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            throw new PostTitleBlankException();
        }
        if (request.getContent() == null || request.getContent().trim().isEmpty()) {
            throw new PostContentBlankException();
        }

        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        postRepository.save(post);
    }

    @Override
    @Transactional
    public void deletePost(Long postId, Member member) {
        Post post = getOnePost(postId);
        
        if (!post.getAuthor().equals(member)) {
            throw new PostNoPermissionException();
        }

        postRepository.delete(post);
    }
} 