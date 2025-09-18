package com.simplesdental.product.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class ProductV2DTO {
    @Schema(
            description = "Identificador único do produto",
            example = "1"
    )
    private Long id;

    @Schema(
            description = "Nome do Produto",
            example = "Playstation 5",
            maximum = "100",
            requiredMode = RequiredMode.REQUIRED
    )
    private String name;

    @Schema(
            description = "Descricao do produto",
            example = "Video game da Sony",
            maximum = "255",
            requiredMode = RequiredMode.NOT_REQUIRED
    )
    private String description;

    @Schema(
            description = "Valor do produto",
            example = "4999.99",
            requiredMode = RequiredMode.REQUIRED
    )
    private BigDecimal price;

    @Schema(
            description = "Situacao do priduto, True para ativo, false para inativo",
            example = "true",
            requiredMode = RequiredMode.REQUIRED
    )
    private Boolean status;

    @Schema(
            description = "Codigo do produto",
            example = "99",
            requiredMode = RequiredMode.NOT_REQUIRED
    )
    private Integer code;

    @Schema(
            description = "Identificador único da categoria do produto",
            example = "1",
            requiredMode = RequiredMode.REQUIRED
    )
    private Long categoryId;

}