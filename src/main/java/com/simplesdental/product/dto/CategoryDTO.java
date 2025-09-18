package com.simplesdental.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record CategoryDTO(
        @Schema(
                description = "Identificador único da categoria",
                example = "1"
        )
        Long id,
        @Schema(
                description = "Nome da categoria",
                example = "Eletrônicos"
        )
        String name,
        @Schema(
                description = "Descricao da categoria",
                example = "Produtos eletrônicos e gadgets"
        )
        String description
) {}
