package apptive.devlog.domain.comment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.util.List;

public class CommentDto {
    @Builder
    public record Create(@NotBlank String text, String parentId) {}

    @Builder
    public record Update(@NotBlank String text) {}

    @Builder
    public record Response(String id, String text, String authorId, String parentId, List<Response> replies) {}
}
