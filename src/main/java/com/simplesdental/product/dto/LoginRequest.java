package com.simplesdental.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record LoginRequest(
        @Schema(
                description = "Email para login",
                example = "Odair@gmail.com"
        )
        String email,
        @Schema(
                description = "senha de acesso",
                example = "123dadw@d123@##"
        )
        String password
) {
}
