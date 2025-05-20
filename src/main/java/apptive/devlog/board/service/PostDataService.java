package apptive.devlog.board.service;

import apptive.devlog.board.entity.PostData;
import apptive.devlog.board.repository.PostDataRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostDataService {
    private final PostDataRepository postDataRepository;

    public PostData create(PostData data) {
        return postDataRepository.save(data);
    }

    public PostData update(Long id, PostData updated) {
        PostData data = postDataRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("해당 게시글을 찾을 수 없습니다.")
        );
        data.setTitle(updated.getTitle());
        data.setContent(updated.getContent());
        return postDataRepository.save(data);
    }

    // 삭제 - soft delete
    public void delete(Long id) {
        postDataRepository.deleteById(id);
    }

    // 단일 조회. 삭제 요소도 조회 가능
    public PostData get(Long id) {
        return postDataRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("해당 게시글을 찾을 수 없습니다.")
        );
    }

    // 모든 데이터 조회
    public List<PostData> getAll() {
        return postDataRepository.findAll();
    }
}
