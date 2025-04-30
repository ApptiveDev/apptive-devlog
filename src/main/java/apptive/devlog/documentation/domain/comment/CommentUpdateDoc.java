package apptive.devlog.documentation.domain.comment;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@Operation(summary = "댓글 수정", description = "본인의 댓글을 수정합니다.")
@SecurityRequirement(name = "bearerAuth")
public @interface CommentUpdateDoc {
}
