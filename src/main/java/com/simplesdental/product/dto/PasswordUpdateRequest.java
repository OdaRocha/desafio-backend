package com.simplesdental.product.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record PasswordUpdateRequest(
        @Schema(
                description = "Atual senha de acesso",
                example = "awidwa@123dawd"
        )
        String oldPassword,
        @Schema(
                description = "Nova senha de acesso (Deve ser diferente da atual)",
                example = "awidwa@123dawd"
        )
        String newPassword
) {
}
