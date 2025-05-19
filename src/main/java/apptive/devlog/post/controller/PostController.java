package apptive.devlog.post.controller;

import apptive.devlog.member.dto.MemberDetails;
import apptive.devlog.member.service.MemberService;
import apptive.devlog.post.dto.*;
import apptive.devlog.post.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping("/posts")
    public ResponseEntity<PostResponse> createPost(@Valid @RequestBody CreatePostRequest post,
                                                   @AuthenticationPrincipal MemberDetails memberDetails) {
        PostResponse response = postService.save(post, memberDetails.getMember().getEmail());

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequestUri()
                .replacePath("/users/{nickname}/post/{id}")
                .buildAndExpand(response.getAuthor(), response.getId())
                .toUri();

        return ResponseEntity.status(HttpStatus.CREATED).location(location).body(response);
    }

    @PatchMapping("/posts/{id}")
    public ResponseEntity<Map<String,String>> updatePost(@Valid @RequestBody UpdatePostRequest post,
                                                         @PathVariable Long id,
                                                         @AuthenticationPrincipal MemberDetails member) {
        postService.updatePost(id,member.getUsername(), post);
        return ResponseEntity.status(HttpStatus.OK).body(Map.of("message","게시글 수정 성공"));
    }

    @DeleteMapping("/posts/{id}")
    public ResponseEntity<Map<String,String>> deletePost(@AuthenticationPrincipal MemberDetails member,
                                                         @PathVariable Long id) {
        postService.deletePost(id,member.getUsername());

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(Map.of("message", "게시글 삭제 완료"));
    }


    @GetMapping("/posts/{id}")
    public ResponseEntity<PostWithCommentResponse> getPost(@PathVariable Long id, @PageableDefault(page=0, size = 20) Pageable pageable) {
        PostWithCommentResponse post = postService.findPost(id, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(post);
    }


    @GetMapping("/users/{nickname}/posts")
    public ResponseEntity<PostPageResponse> memberPosts(@PathVariable String nickname,
                                                        @RequestParam(required = false) String title,
                                                        @PageableDefault(page=0, size = 10) Pageable pageable) {
        PostPageResponse response = postService.findPostsByMember(nickname, pageable, title);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


}
