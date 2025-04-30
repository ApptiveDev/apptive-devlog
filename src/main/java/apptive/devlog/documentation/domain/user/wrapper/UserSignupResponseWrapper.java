package apptive.devlog.documentation.domain.user.wrapper;

import apptive.devlog.common.response.success.CommonResponse;
import apptive.devlog.domain.auth.dto.UserSignupResponseDto;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "UserSignupResponseWrapper")
public class UserSignupResponseWrapper extends CommonResponse<UserSignupResponseDto> {}
