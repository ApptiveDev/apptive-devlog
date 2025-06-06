package apptive.devlog.Post.Service;

import apptive.devlog.Member.Domain.Member;
import apptive.devlog.Post.Domain.Post;
import apptive.devlog.Post.Dto.CreatePostRequest;
import apptive.devlog.Post.Dto.UpdatePostRequest;

import java.util.List;

public interface PostService {
    Long createPost(CreatePostRequest request, Member member);
    Post getOnePost(Long postId);

    List<Post> getPostsByTitle(String title);

    List<Post> getPostsByContent(String content);

    List<Post> getMyPosts(Member Author);
    List<Post> getPostsByAuthor(String nickname);

    List<Post> getPostsByTitleAndContent(String title,String content);


    void updatePost(Long postId, UpdatePostRequest request, Member member);
    void deletePost(Long postId, Member member);
} 