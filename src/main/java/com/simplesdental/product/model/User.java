package com.simplesdental.product.model;

import com.simplesdental.product.dto.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Entity
@Table(name = "users")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Email(message = "Email deve ser válido")
    private String email;

    @Column(nullable = false)
    private String password;

    @Pattern(regexp = "^(ADMIN|USER)$", message = "Role deve ser 'ADMIN' ou 'USER'")
    @Column(nullable = false, length = 10)
    private String role;
}