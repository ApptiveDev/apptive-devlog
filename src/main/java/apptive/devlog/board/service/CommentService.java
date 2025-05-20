package apptive.devlog.board.service;

import apptive.devlog.board.entity.Comment;
import apptive.devlog.board.entity.Post;
import apptive.devlog.board.repository.CommentRepository;
import apptive.devlog.board.repository.PostRepository;
import apptive.devlog.mail.service.MailService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostService postService;
    private final MailService mailService;

    // 댓글 생성, 이메일 notify
    public Comment createAndNotify(Long postId, Comment comment){
        Post post = postService.get(postId);
        comment.setPost(post);

        comment.setCreatedAt(LocalDate.now());
        comment.setUpdatedAt(LocalDate.now());
        comment.setIsDeleted(false);
        comment.setLikeCount(0L);

        Comment savedComment = commentRepository.save(comment);

        //이메일 전송
        mailService.sendMimeMessage(post.getUser().getEmail());

        return savedComment;
    }

    // 수정
    public Comment update(Long id, Comment updated){
        Comment comment = commentRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("해당 댓글을 찾을 수 없습니다.")
        );
        comment.setUpdatedAt(LocalDate.now());
        comment.setIsDeleted(updated.getIsDeleted());
        comment.setLikeCount(updated.getLikeCount());
        return commentRepository.save(comment);
    }

    // 삭제 - soft delete
    public void delete(Long id){
        Comment comment = commentRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("해당 댓글을 찾을 수 없습니다.")
        );
        comment.setIsDeleted(true);
        commentRepository.save(comment);
    }

    // 단일 조회. 삭제 요소도 조회 가능
    public Comment get(Long id){
        return commentRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("해당 댓글을 찾을 수 없습니다.")
        );
    }


    // 작성된 모든 댓글 조회
    public List<Comment> getAll(){
        return commentRepository.findAll();
    }

    // 유효한 댓글만 조회
    public List<Comment> getNotDeleted(){
        return commentRepository.findByIsDeletedFalse();
    }

    public List<Comment> getValidCommentByPostId(Long postId){
        return commentRepository.findValidByPostId(postId);
    }
}
