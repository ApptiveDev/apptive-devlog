package apptive.devlog.documentation.domain.comment;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@Operation(summary = "댓글 작성", description = "게시글에 댓글을 작성합니다.")
@SecurityRequirement(name = "bearerAuth")
public @interface CommentCreateDoc {
}
