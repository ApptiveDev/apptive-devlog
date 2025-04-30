package apptive.devlog.documentation.domain.auth;

import apptive.devlog.common.response.success.CommonResponse;
import apptive.devlog.documentation.domain.user.wrapper.UserSignupResponseWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@Operation(summary = "회원 가입", description = "회원으로 가입합니다.")
@ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "회원가입 성공",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = UserSignupResponseWrapper.class)
                )),
        @ApiResponse(responseCode = "400", description = "요청 파라미터 오류 또는 중복된 이메일",
                content = @Content(schema = @Schema(implementation = CommonResponse.class)))
})
public @interface AuthSignupDoc {
}
