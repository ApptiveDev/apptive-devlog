package apptive.devlog.documentation.domain.comment;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@Operation(summary = "댓글 삭제", description = "본인의 댓글을 삭제합니다.")
@SecurityRequirement(name = "bearerAuth")
public @interface CommentDeleteDoc {
}
