package com.simplesdental.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class UserDTO {

    @Schema(
            description = "Identificador único do usuario",
            example = "1000"
    )
    final Long id;
    @Schema(
            description = "Senha de acesso",
            example = "1298367asd80712!@#"
    )
    String password;
    @Schema(
            description = "Nome do usuario",
            example = "Odair Rocha"
    )
    final String name;
    @Schema(
            description = "Email de acesso",
            example = "Odair@gmail.com"
    )
    final String email;
    @Schema(
            description = "Papel do usuario",
            example = "USER|ADMIN"
    )
    final Role role;

    public UserDTO(
            @Schema(
                    description = "Identificador único do usuario",
                    example = "1000"
            )
            Long id,
            @Schema(
                    description = "Senha de acesso",
                    example = "1298367asd80712!@#"
            )
            String password,
            @Schema(
                    description = "Nome do usuario",
                    example = "Odair Rocha"
            )
            String name,
            @Schema(
                    description = "Email de acesso",
                    example = "Odair@gmail.com"
            )
            String email,
            @Schema(
                    description = "Papel do usuario",
                    example = "USER|ADMIN"
            )
            Role role
    ){
        this.id = id;
        this.password = password;
        this.name = name;
        this.email = email;
        this.role = role;
    }

    public void changePassword(String newPassword) {
        password = newPassword;
    }


}
