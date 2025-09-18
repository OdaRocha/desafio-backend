package com.simplesdental.product.service;

import br.com.fluentvalidator.context.ValidationResult;
import com.simplesdental.product.client.RedisClient;
import com.simplesdental.product.dto.*;
import com.simplesdental.product.exception.*;
import com.simplesdental.product.validator.LoginValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.simplesdental.product.dto.Permission.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private LoginValidator validator;

    @Mock
    private RedisClient redisClient;

    @InjectMocks
    private AuthService authService;


    @Test
    void deveLancarValidationExceptionQuandoDadosInvalidos() {
        LoginRequest request = new LoginRequest("email@invalido.com", "");
        ValidationResult validationResult = mock(ValidationResult.class);
        when(validationResult.isValid()).thenReturn(false);

        when(validator.validate(request)).thenReturn(validationResult);

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            authService.login(request);
        });

        verify(validator).validate(request);
        verify(userService, never()).findByEmail(anyString());
        verify(redisClient, never()).login(any());
    }

    @Test
    void deveLancarInvalidCredentialsExceptionQuandoCredenciaisInvalidas() {
        LoginRequest request = new LoginRequest("usuario@email.com", "senha123");
        ValidationResult validationResult = mock(ValidationResult.class);
        when(validationResult.isValid()).thenReturn(true);

        when(validator.validate(request)).thenReturn(validationResult);
        when(userService.findByEmail(request.email())).thenReturn(Optional.empty());

        InvalidCredentialsException exception = assertThrows(InvalidCredentialsException.class, () -> {
            authService.login(request);
        });

        assertEquals("Credenciais inválidas.", exception.getMessage());
        verify(validator).validate(request);
        verify(userService).findByEmail(request.email());
        verify(redisClient, never()).login(any());
    }

    @Test
    void deveLancarInvalidCredentialsExceptionQuandoSenhaIncorreta() {
        LoginRequest request = new LoginRequest("usuario@email.com", "senha_errada");
        ValidationResult validationResult = mock(ValidationResult.class);
        when(validationResult.isValid()).thenReturn(true);

        UserDTO user = new UserDTO(1L, "senha_correta", null, "usuario@email.com", Role.USER);

        when(validator.validate(request)).thenReturn(validationResult);
        when(userService.findByEmail(request.email())).thenReturn(Optional.of(user));

        InvalidCredentialsException exception = assertThrows(InvalidCredentialsException.class, () -> {
            authService.login(request);
        });

        assertEquals("Credenciais inválidas.", exception.getMessage());
        verify(validator).validate(request);
        verify(userService).findByEmail(request.email());
        verify(redisClient, never()).login(any());
    }

    @Test
    void deveRealizarLoginComSucesso() {
        LoginRequest request = new LoginRequest("usuario@email.com", "senha123");
        ValidationResult validationResult = mock(ValidationResult.class);
        when(validationResult.isValid()).thenReturn(true);

        UserDTO user = new UserDTO(1L, "senha123", "senha_correta", null, Role.USER);

        when(validator.validate(request)).thenReturn(validationResult);
        when(userService.findByEmail(request.email())).thenReturn(Optional.of(user));

        authService.login(request);

        verify(validator).validate(request);
        verify(userService).findByEmail(request.email());
        verify(redisClient).login(any(AuthContextResponse.class));
    }

    @Test
    void deveLancarUnauthorizedExceptionQuandoContextEhNulo() {
        UserDTO userDTO = new UserDTO(1L, "senha123", "Usuario Teste", "teste@email.com", Role.USER);

        when(redisClient.getAuthContext()).thenReturn(null);

        UnauthorizedException exception = assertThrows(UnauthorizedException.class, () -> {
            authService.register(userDTO);
        });

        assertEquals("Necessario realizar login!", exception.getMessage());
        verify(redisClient).getAuthContext();
        verify(userService, never()).save(any());
    }

    @Test

    void deveLancarForbiddenExceptionQuandoNaoTemPermissao() {
        UserDTO userDTO = new UserDTO(1L, "senha123", "Usuario Teste", "teste@email.com", Role.USER);
        AuthContextResponse context = new AuthContextResponse(1L, "admin@email.com", Role.USER);

        when(redisClient.getAuthContext()).thenReturn(context);

        ForbiddenException exception = assertThrows(ForbiddenException.class, () -> {
            authService.register(userDTO);
        });

        assertEquals("Usuário não autorizado a criar novos usuários.", exception.getMessage());
        verify(redisClient).getAuthContext();
        verify(userService, never()).save(any());
    }

    @Test
    void deveSalvarUsuarioComSucessoQuandoTemPermissao() {
        UserDTO userDTO = new UserDTO(1L, "senha123", "Usuario Teste", "teste@email.com", Role.USER);
        UserDTO savedUser = new UserDTO(2L, "senha123", "Usuario Teste", "teste@email.com", Role.USER);
        AuthContextResponse context = new AuthContextResponse(1L, "admin@email.com", Role.ADMIN);

        when(redisClient.getAuthContext()).thenReturn(context);
        when(userService.save(userDTO)).thenReturn(savedUser);

        UserDTO result = authService.register(userDTO);

        assertEquals(savedUser.getId(), result.getId());
        assertEquals(savedUser.getName(), result.getName());
        assertEquals(savedUser.getEmail(), result.getEmail());
        verify(redisClient).getAuthContext();
        verify(userService).save(userDTO);
    }

    @Test
    void deveLancarUnauthorizedExceptionQuandoContextEhNuloNoGetContext() {
        when(redisClient.getAuthContext()).thenReturn(null);

        UnauthorizedException exception = assertThrows(UnauthorizedException.class, () -> {
            authService.getContext();
        });

        assertEquals("Usuário não autenticado.", exception.getMessage());
        verify(redisClient).getAuthContext();
    }

    @Test
    void deveRetornarContextQuandoUsuarioAutenticado() {
        AuthContextResponse context = new AuthContextResponse(1L, "admin@email.com", Role.ADMIN);

        when(redisClient.getAuthContext()).thenReturn(context);

        AuthContextResponse result = authService.getContext();

        assertEquals(context.id(), result.id());
        assertEquals(context.email(), result.email());
        assertEquals(context.role(), result.role());
        verify(redisClient).getAuthContext();
    }

    @Test
    void deveLancarForbiddenExceptionQuandoNaoTemPermissaoNoCheckRole() {
        AuthContextResponse context = new AuthContextResponse(1L, "user@email.com", Role.USER);

        when(redisClient.getAuthContext()).thenReturn(context);

        ForbiddenException exception = assertThrows(ForbiddenException.class, () -> {
            authService.checkRole(CREATE_USER);
        });

        assertEquals("Usuário não autorizado.", exception.getMessage());
        verify(redisClient).getAuthContext();
    }

    @Test
    void devePassarQuandoTemPermissaoNoCheckRole() {
        AuthContextResponse context = new AuthContextResponse(1L, "admin@email.com", Role.ADMIN);

        when(redisClient.getAuthContext()).thenReturn(context);

        authService.checkRole(CREATE_USER);

        verify(redisClient).getAuthContext();
    }

    @Test
    void deveLancarInvalidCredentialsExceptionQuandoSenhaAtualIncorreta() {
        PasswordUpdateRequest request = new PasswordUpdateRequest("senha_errada", "nova_senha");
        AuthContextResponse context = new AuthContextResponse(1L, "user@email.com", Role.USER);
        UserDTO user = new UserDTO(1L, "senha_correta", "Usuario Teste", "user@email.com", Role.USER);

        when(redisClient.getAuthContext()).thenReturn(context);
        when(userService.findByEmail(context.email())).thenReturn(Optional.of(user));

        InvalidCredentialsException exception = assertThrows(InvalidCredentialsException.class, () -> {
            authService.updatePassword(request);
        });

        assertEquals("Senha atual incorreta.", exception.getMessage());
        verify(redisClient).getAuthContext();
        verify(userService).findByEmail(context.email());
        verify(userService, never()).save(any());
        verify(redisClient, never()).deleteValue();
    }

    @Test
    void deveLancarBusinessExceptionQuandoUsuarioNaoEncontrado() {
        PasswordUpdateRequest request = new PasswordUpdateRequest("senha_atual", "nova_senha");
        AuthContextResponse context = new AuthContextResponse(1L, "user@email.com", Role.USER);

        when(redisClient.getAuthContext()).thenReturn(context);
        when(userService.findByEmail(context.email())).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            authService.updatePassword(request);
        });

        assertEquals("Usuário não encontrado.", exception.getMessage());
        verify(redisClient).getAuthContext();
        verify(userService).findByEmail(context.email());
        verify(userService, never()).save(any());
        verify(redisClient, never()).deleteValue();
    }

    @Test
    void deveAtualizarSenhaComSucessoEDeletarDoRedis() {
        PasswordUpdateRequest request = new PasswordUpdateRequest("senha_atual", "nova_senha");
        AuthContextResponse context = new AuthContextResponse(1L, "user@email.com", Role.USER);
        UserDTO user = new UserDTO(1L, "senha_atual", "Usuario Teste", "user@email.com", Role.USER);

        when(redisClient.getAuthContext()).thenReturn(context);
        when(userService.findByEmail(context.email())).thenReturn(Optional.of(user));
        when(userService.save(user)).thenReturn(user);

        authService.updatePassword(request);

        verify(redisClient).getAuthContext();
        verify(userService).findByEmail(context.email());
        verify(userService).save(user);
        verify(redisClient).deleteValue();
    }
}
