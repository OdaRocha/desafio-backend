package com.simplesdental.product.controller;

import com.simplesdental.product.dto.AuthContextResponse;
import com.simplesdental.product.dto.LoginRequest;
import com.simplesdental.product.dto.PasswordUpdateRequest;
import com.simplesdental.product.dto.UserDTO;
import com.simplesdental.product.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Tag(name = "Api de Usuarios", description = "API para gerenciamento de usuarios")
public class UserController {

    private final AuthService authService;

    public UserController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "Realiza login do usuário")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login realizado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Credenciais inválidas")
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        authService.login(request);
        return ResponseEntity.ok().body("Login realizado com sucesso!");
    }

    @Operation(summary = "Registra um novo usuário")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuário registrado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@RequestBody UserDTO userDTO) {
        UserDTO response = authService.register(userDTO);

        return ResponseEntity.status(201).body(response);
    }

    @Operation(summary = "Retorna informações do usuário autenticado")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Dados do usuário retornados"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    @GetMapping("/context")
    public ResponseEntity<AuthContextResponse> getContext() {
        AuthContextResponse context = authService.getContext();
        return ResponseEntity.ok(context);
    }

    @Operation(summary = "Atualiza a senha do usuário autenticado")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Senha atualizada"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    @PutMapping("/users/password")
    public ResponseEntity<?> updatePassword(@RequestBody PasswordUpdateRequest request) {
        authService.updatePassword(request);
        return ResponseEntity.ok().body("Credencial alterada com sucesso!");
    }
}
