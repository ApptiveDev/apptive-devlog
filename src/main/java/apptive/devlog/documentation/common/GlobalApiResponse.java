package apptive.devlog.documentation.common;

import apptive.devlog.common.response.success.CommonResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.lang.annotation.*;

@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ApiResponses({
        @ApiResponse(responseCode = "200", description = "요청 성공", content = @Content(schema = @Schema(implementation = CommonResponse.class))),
        @ApiResponse(responseCode = "201", description = "리소스 생성 성공", content = @Content(schema = @Schema(implementation = CommonResponse.class))),
        @ApiResponse(responseCode = "204", description = "응답 본문 없음", content = @Content(schema = @Schema())),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 (유효성 검증 실패 등)", content = @Content(schema = @Schema(implementation = CommonResponse.class))),
        @ApiResponse(responseCode = "401", description = "인증되지 않음 (토큰 없음 또는 만료)", content = @Content(schema = @Schema(implementation = CommonResponse.class))),
        @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content(schema = @Schema(implementation = CommonResponse.class))),
        @ApiResponse(responseCode = "404", description = "리소스를 찾을 수 없음", content = @Content(schema = @Schema(implementation = CommonResponse.class))),
        @ApiResponse(responseCode = "409", description = "충돌 (중복 데이터 등)", content = @Content(schema = @Schema(implementation = CommonResponse.class))),
        @ApiResponse(responseCode = "422", description = "처리할 수 없는 요청 (비즈니스 로직 실패 등)", content = @Content(schema = @Schema(implementation = CommonResponse.class))),
        @ApiResponse(responseCode = "429", description = "너무 많은 요청", content = @Content(schema = @Schema(implementation = CommonResponse.class))),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류", content = @Content(schema = @Schema(implementation = CommonResponse.class))),
        @ApiResponse(responseCode = "503", description = "서비스 이용 불가", content = @Content(schema = @Schema(implementation = CommonResponse.class)))
})
public @interface GlobalApiResponse {
}
