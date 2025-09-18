package com.simplesdental.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record AuthContextResponse(
        @Schema(
                description = "Id do usuario",
                example = "1234"
        )
        Long id,
        @Schema(
                description = "Email para login",
                example = "Odair@gmail.com"
        )
        String email,
        @Schema(
                description = "Papel do usuario",
                example = "USER|ADMIN"
        )
        Role role
) {
}
