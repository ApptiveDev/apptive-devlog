package apptive.devlog.common.response.success;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "API 공통 응답 포맷")
public class CommonResponse<T> {

    @Schema(description = "요청 성공 여부", example = "true")
    private boolean success;
    @Schema(description = "HTTP 상태 코드", example = "200")
    private int status;
    @Schema(description = "응답 메시지", example = "요청이 성공했습니다.")
    private String message;
    @Schema(description = "응답 데이터", implementation = Object.class)
    private T data;
    @Schema(description = "응답 시간", example = "2024-04-08T12:34:56")
    private LocalDateTime timestamp;

    @Builder
    private CommonResponse(boolean success, HttpStatus httpStatus, String message, T data, LocalDateTime timestamp) {
        this.success = success;
        this.status = httpStatus.value();
        this.message = message;
        this.data = data;
        this.timestamp = timestamp != null ? timestamp : LocalDateTime.now();
    }

    // ===== Success =====

    public static <T> CommonResponse<T> success(HttpStatus status, String message, T data) {
        return CommonResponse.<T>builder()
                .success(true)
                .httpStatus(status)
                .message(message)
                .data(data)
                .build();
    }

    public static <T> CommonResponse<T> success(HttpStatus status, T data) {
        return CommonResponse.<T>builder()
                .success(true)
                .httpStatus(status)
                .message("요청이 성공했습니다.")
                .data(data)
                .build();
    }

    public static CommonResponse<Void> success(HttpStatus status, String message) {
        return CommonResponse.<Void>builder()
                .success(true)
                .httpStatus(status)
                .message(message)
                .build();
    }

    public static CommonResponse<Void> success(HttpStatus status) {
        return CommonResponse.<Void>builder()
                .success(true)
                .httpStatus(status)
                .message("요청이 성공했습니다.")
                .build();
    }

    // 기본 200 OK
    public static <T> CommonResponse<T> ok(T data) {
        return success(HttpStatus.OK, data);
    }

    public static <T> CommonResponse<T> created(T data) {
        return success(HttpStatus.CREATED, data);
    }

    public static CommonResponse<Void> noContent() {
        return success(HttpStatus.NO_CONTENT, (Void) null);
    }

    // ===== Error =====

    public static CommonResponse<Void> error(HttpStatus status, String message) {
        return CommonResponse.<Void>builder()
                .success(false)
                .httpStatus(status)
                .message(message)
                .build();
    }

    public static <T> CommonResponse<T> error(HttpStatus status, String message, T data) {
        return CommonResponse.<T>builder()
                .success(false)
                .httpStatus(status)
                .message(message)
                .data(data)
                .build();
    }
}
