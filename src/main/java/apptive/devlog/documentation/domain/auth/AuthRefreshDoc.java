package apptive.devlog.documentation.domain.auth;

import apptive.devlog.documentation.domain.user.wrapper.UserRefreshResponseWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@Operation(summary = "토큰 재발급", description = "Refresh Token을 이용하여 Access Token을 재발급합니다.")
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "토큰 재발급 성공",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = UserRefreshResponseWrapper.class)
                )),
        @ApiResponse(responseCode = "401", description = "유효하지 않은 토큰", content = @Content)
})
@SecurityRequirement(name = "bearerAuth")
public @interface AuthRefreshDoc {
}
