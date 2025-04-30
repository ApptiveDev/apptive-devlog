package apptive.devlog.documentation.domain.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@Operation(summary = "유저 프로필 조회", description = "이메일을 기반으로 유저 프로필 정보를 조회합니다.")
@ApiResponses(value = {
        @ApiResponse(
                responseCode = "200",
                description = "조회 성공",
                content = @Content(schema = @Schema(implementation = ApiResponse.class))
        )
})
@Parameter(
        name = "requestDto",
        description = "조회할 유저의 이메일",
        example = "test@example.com"
)
@SecurityRequirement(name = "bearerAuth")
public @interface UserProfileDoc {
}
