package apptive.devlog.board.service;

import apptive.devlog.auth.entity.User;
import apptive.devlog.auth.repository.UserRepository;
import apptive.devlog.board.entity.Post;
import apptive.devlog.board.entity.PostData;
import apptive.devlog.board.repository.PostDataRepository;
import apptive.devlog.board.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;

    // 생성 후 이메일 전송
    public Post create(Post post){
        post.setCreatedAt(LocalDate.now());
        post.setUpdatedAt(LocalDate.now());
        post.setIsDeleted(false);
        post.setLikeCount(0L);

        return postRepository.save(post);
    }

    // 수정
    public Post update(Long id, Post updated){
        Post post = postRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("해당 게시글을 찾을 수 없습니다."));
        post.setUpdatedAt(LocalDate.now());
        post.setLikeCount(updated.getLikeCount());
        post.setIsDeleted(updated.getIsDeleted());
        return postRepository.save(post);
    }

    // 삭제 - soft delete
    public void delete(Long id) {
        Post post = postRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("해당 게시글을 찾을 수 없습니다.")
        );
        post.setIsDeleted(true);
        postRepository.save(post);
    }

    // 단일 조회. 삭제 요소도 조회 가능
    public Post get(Long id){
        return postRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("해당 게시글을 찾을 수 없습니다."));
    }

    public User getUserById(Long id){
        Post post = postRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("해당 게시글을 찾을 수 없습니다.")
        );
        return post.getUser();
    }

    //모든 게시글 조회
    public List<Post> getAll() {
        return postRepository.findAll();
    }

    // 유효한 게시글만 조회
    public List<Post> getNotDeleted() {
        return postRepository.findByIsDeletedFalse();
    }
}
