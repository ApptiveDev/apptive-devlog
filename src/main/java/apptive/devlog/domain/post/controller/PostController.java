package apptive.devlog.domain.post.controller;

import apptive.devlog.common.response.success.CommonResponse;
import apptive.devlog.documentation.domain.post.PostCreateDoc;
import apptive.devlog.documentation.domain.post.PostDeleteDoc;
import apptive.devlog.documentation.domain.post.PostReadDoc;
import apptive.devlog.documentation.domain.post.PostUpdateDoc;
import apptive.devlog.documentation.tags.PostDocumentation;
import apptive.devlog.domain.post.dto.PostDto;
import apptive.devlog.domain.post.service.PostService;
import apptive.devlog.domain.user.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostController implements PostDocumentation {
    private final PostService postService;

    @PostCreateDoc
    @PostMapping
    public ResponseEntity<CommonResponse<PostDto.Response>> create(@Valid @RequestBody PostDto.Create postDto, @RequestAttribute("user") User user) {
        PostDto.Response responseDto = postService.create(postDto, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(CommonResponse.created(responseDto));
    }

    @PostReadDoc
    @GetMapping("/{id}")
    public ResponseEntity<CommonResponse<PostDto.Response>> read(@PathVariable String id) {
        return ResponseEntity.ok().body(CommonResponse.ok(postService.read(id)));
    }

    @PostUpdateDoc
    @PutMapping("/{id}")
    public ResponseEntity<CommonResponse<PostDto.Response>> update(@PathVariable String id, @Valid @RequestBody PostDto.Update dto, @RequestAttribute("user") User user) {
        PostDto.Response response = postService.update(id, dto, user);
        return ResponseEntity.ok().body(CommonResponse.ok(response));
    }

    @PostDeleteDoc
    @DeleteMapping("/{id}")
    public ResponseEntity<CommonResponse<PostDto.Response>> delete(@PathVariable String id, @RequestAttribute("user") User user) {
        postService.delete(id, user);
        return ResponseEntity.noContent().build();
    }
}
