package apptive.devlog.documentation.domain.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@Operation(summary = "로그아웃", description = "로그아웃 처리합니다.")
@ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "로그아웃 성공", content = @Content)
})
@SecurityRequirement(name = "bearerAuth")
public @interface AuthLogoutDoc {
}
