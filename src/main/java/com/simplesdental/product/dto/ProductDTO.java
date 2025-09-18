package com.simplesdental.product.dto;


import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

public record ProductDTO(
        @Schema(
                description = "Identificador único do produto",
                example = "1"
        )
        Long id,
        @Schema(
                description = "Nome do Produto",
                example = "Playstation 5",
                maximum = "100",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String name,
        @Schema(
                description = "Descricao do produto",
                example = "Video game da Sony",
                maximum = "255",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        String description,
        @Schema(
                description = "Valor do produto",
                example = "4999.99",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        BigDecimal price,
        @Schema(
                description = "Situacao do priduto, True para ativo, false para inativo",
                example = "true",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        Boolean status,
        @Schema(
                description = "Codigo do produto",
                example = "PROD-99",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        String code,
        @Schema(
                description = "Identificador único da categoria do produto",
                example = "1",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        Long categoryId
) {
    private static final String CODE_PREFIX = "PROD-";

    public static ProductDTO of(
            Long id,
            String name,
            String description,
            BigDecimal price,
            Boolean status,
            Integer code,
            Long categoryId
    ) {
        String formattedCode = CODE_PREFIX + code;

        return new ProductDTO(id, name, description, price, status, formattedCode, categoryId);
    }
}
