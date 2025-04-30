package apptive.devlog.documentation.domain.auth;

import apptive.devlog.documentation.domain.user.wrapper.UserLoginResponseWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@Operation(summary = "회원 로그인", description = "이메일과 비밀번호로 로그인합니다.")
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "로그인 성공",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = UserLoginResponseWrapper.class)
                )),
        @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content)
})
public @interface AuthLoginDoc {
}
