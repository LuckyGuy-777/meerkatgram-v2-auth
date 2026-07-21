package com.meerkatgramv2auth.domain.auth.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Schema(description = "로그인시 필요 데이터") // 스웨거 어노테이션
public record LoginRequestDTO(
        @Schema(description = "이메일", example = "test@test.com", nullable = false, requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "이메일은 필수 입니다")
        @Pattern(regexp = "^[0-9a-zA-Z](?!.*?[\\-_.]{2})[a-zA-Z0-9\\-_.]{3,63}@[0-9a-zA-Z](?!.*?[\\-_.]{2})[a-zA-Z0-9\\-_.]{3,63}\\.[a-zA-Z]{2,3}$")
        String email,

        @Schema(description = "비밀번호", example = "qwer1234", nullable = false, requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "비밀번호는 필수 입니다.")
        @Pattern(regexp = "^[0-9a-zA-Z!@#$%^&*()]{8,20}$", message = "허용하지 않는 양식입니다")
        String password
) {
}
