package apptive.devlog.post.service;

import apptive.devlog.comment.dto.CommentRequest;
import apptive.devlog.comment.dto.CommentResponse;
import apptive.devlog.domain.Gender;
import apptive.devlog.domain.Member;
import apptive.devlog.domain.Post;
import apptive.devlog.member.dto.JoinForm;
import apptive.devlog.member.repository.MemberRepository;
import apptive.devlog.member.service.MemberService;
import apptive.devlog.post.dto.CreatePostRequest;
import apptive.devlog.post.dto.PostResponse;
import apptive.devlog.post.dto.PostWithCommentResponse;
import apptive.devlog.post.dto.UpdatePostRequest;
import apptive.devlog.post.repository.PostRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;


@SpringBootTest
@Transactional
class PostServiceTest {

    @Autowired
    PostService postService;

    @Autowired
    PostRepository postRepository;

    @Autowired
    MemberService memberService;
    @Autowired
    MemberRepository memberRepository;

    @Test
    void createPost() {

        Member member = makeMember();

        CreatePostRequest post = new CreatePostRequest("제목1", "내용1");
        PostResponse response = postService.save(post, member.getEmail());

        assertThat(response.getAuthor()).isEqualTo(member.getNickname());
    }

    @Test
    void deletePost() {
        Member member = makeMember();

        CreatePostRequest post = new CreatePostRequest("제목1", "내용1");
        PostResponse response = postService.save(post, member.getEmail());

        postService.deletePost(response.getId(), member.getEmail());

        assertThat(postRepository.findById(response.getId())).isEmpty();
    }

    @Test
    void updatePost() {
        Member member = makeMember();

        CreatePostRequest post = new CreatePostRequest("제목1", "내용1");
        PostResponse response = postService.save(post, member.getEmail());

        UpdatePostRequest updatePost = new UpdatePostRequest("제목2", "내용2");

        postService.updatePost(response.getId(),member.getEmail(), updatePost);

        Post findPost = postRepository.findById(response.getId()).get();

        assertThat(findPost.getTitle()).isEqualTo(updatePost.getTitle());
        assertThat(findPost.getContent()).isEqualTo(updatePost.getContent());
    }

    @Test
    void getPost() {
        Member member = makeMember();
        CreatePostRequest post = new CreatePostRequest("제목1", "내용1");
        PostResponse response = postService.save(post, member.getEmail());

        PostWithCommentResponse findResponse = postService.findPost(response.getId(), PageRequest.of(1,30));

        assertThat(findResponse.getId()).isEqualTo(response.getId());
        assertThat(findResponse.getAuthor()).isEqualTo(member.getNickname());
    }

    @Test
    void showMemberPosts() {
        Member member = makeMember();
        makePosts(member);

        assertThat(postService.findPostsByMember(member.getNickname(), PageRequest.of(0,20), null).getPosts().size()).isEqualTo(10);
    }

    private void makePosts(Member member) {
        for (int i = 1; i <= 10; i++) {
            postService.save(new CreatePostRequest("제목"+ i, "내용" + i), member.getEmail());
        }
    }



    private Member makeMember() {
        JoinForm joinForm = joinTestCase();
        memberService.join(joinForm);
        return memberRepository.findByNickname(joinForm.getNickname()).orElse(null);
    }

    private JoinForm joinTestCase() {
        JoinForm joinForm =
                new JoinForm("ljw2109@naver.com", "Qwer1234!!","Qwer1234!!", "이진원",
                        "이진원", LocalDate.of(2001,6,26), Gender.MALE);
        return joinForm;
    }
}