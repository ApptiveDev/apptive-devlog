package apptive.devlog.board.service;

import apptive.devlog.board.entity.Reply;
import apptive.devlog.board.repository.ReplyRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class ReplyService {
    private final ReplyRepository replyRepository;

    public Reply create(Reply reply){
        reply.setCreatedAt(LocalDate.now());
        reply.setCreatedAt(LocalDate.now());
        reply.setIsDeleted(false);
        reply.setLikeCount(0L);

        return replyRepository.save(reply);
    }

    public Reply update(Long id, Reply updated) {
        Reply reply = replyRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("해당 답글을 찾을 수 없습니다.")
        );
        reply.setUpdatedAt(LocalDate.now());
        reply.setLikeCount(updated.getLikeCount());
        reply.setIsDeleted(updated.getIsDeleted());
        return replyRepository.save(reply);
    }

    // 삭제 - soft delete
    public void delete(Long id) {
        Reply reply = replyRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("해당 답글을 찾을 수 없습니다.")
        );

        reply.setIsDeleted(true);
        replyRepository.save(reply);
    }

    public Reply get(Long id){
        return replyRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("해당 게시글을 찾을 수 없습니다."));
    }

}
