package apptive.devlog.domain.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class EmailVerifyRequestDto {
    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String code;
}