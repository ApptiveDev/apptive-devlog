package apptive.devlog.domain.oauth2.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OAuth2CallbackRequestDto {
    @NotBlank
    private String code;
    @NotBlank
    private String state;
    @NotBlank
    private String provider;
}
