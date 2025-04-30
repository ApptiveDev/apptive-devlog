package apptive.devlog.documentation.domain.post;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@Operation(summary = "게시글 작성")
@SecurityRequirement(name = "bearerAuth")
public @interface PostCreateDoc {
}
