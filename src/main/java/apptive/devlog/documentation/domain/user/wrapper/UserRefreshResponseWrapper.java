package apptive.devlog.documentation.domain.user.wrapper;

import apptive.devlog.common.response.success.CommonResponse;
import apptive.devlog.domain.auth.dto.UserRefreshResponseDto;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "UserRefreshResponseWrapper")
public class UserRefreshResponseWrapper extends CommonResponse<UserRefreshResponseDto> {}

