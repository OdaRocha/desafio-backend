package com.simplesdental.product.service;

import br.com.fluentvalidator.context.ValidationResult;
import com.simplesdental.product.client.RedisClient;
import com.simplesdental.product.dto.*;
import com.simplesdental.product.exception.*;
import com.simplesdental.product.validator.LoginValidator;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@Slf4j
@Service
public class AuthService {

    private final UserService userService;
    private final LoginValidator validator;
    private final RedisClient redisClient;

    public AuthService(UserService userService, LoginValidator validator, RedisClient client) {
        this.userService = userService;
        this.validator = validator;
        this.redisClient = client;
    }

    public void login(LoginRequest request) {
        log.info("Tentando login para o email: {}", request.email());
        ValidationResult validation = validator.validate(request);

        if(!validation.isValid()) {
            log.error("Erro de validação no login para o email: {} - {}", request.email(), validation.getErrors());
            throw new ValidationException(validation);
        }

        Optional<UserDTO> userOpt = userService.findByEmail(request.email());

        if(userOpt.isEmpty() || !request.password().equals(userOpt.get().getPassword())) {
            log.error("Credenciais inválidas para o email: {}", request.email());
            throw new InvalidCredentialsException("Credenciais inválidas.");
        }

        UserDTO user = userOpt.get();
        log.info("Login realizado com sucesso para o email: {}", user.getEmail());
        AuthContextResponse auth = new AuthContextResponse(user.getId(), user.getEmail(), user.getRole());

        redisClient.login(auth);
    }

    public UserDTO register(UserDTO userDTO) {
        log.info("Tentando registrar novo usuário: {}", userDTO.getEmail());
        AuthContextResponse context = redisClient.getAuthContext();

        if (context == null) {
            log.error("Tentativa de registro sem autenticação");
            throw new UnauthorizedException("Necessario realizar login!");
        }

        if (!context.role().hasPermission(Permission.CREATE_USER)) {
            log.error("Usuário {} não tem permissão para criar novos usuários.", context.email());
            throw new ForbiddenException("Usuário não autorizado a criar novos usuários.");
        }

        UserDTO saved = userService.save(userDTO);
        log.info("Usuário registrado com sucesso: {}", saved.getEmail());
        return saved;
    }

    public AuthContextResponse getContext() {
        AuthContextResponse context = redisClient.getAuthContext();

        if (context == null) {
            log.error("Usuário não autenticado ao tentar obter contexto");
            throw new UnauthorizedException("Usuário não autenticado.");
        }

        return context;
    }

    @Transactional
    public void updatePassword(@RequestBody PasswordUpdateRequest request) {
        AuthContextResponse context = getContext();
        log.info("Usuário {} solicitou alteração de senha", context.email());
        userService.findByEmail(context.email()).ifPresentOrElse(user -> {
            if(!user.getPassword().equals(request.oldPassword())) {
                log.error("Senha atual incorreta para o usuário: {}", context.email());
                throw new InvalidCredentialsException("Senha atual incorreta.");
            }

            user.changePassword(request.newPassword());
            userService.save(user);
            log.info("Senha alterada com sucesso para o usuário: {}", context.email());
        }, () -> {
            log.error("Usuário não encontrado ao tentar alterar senha: {}", context.email());
            throw new BusinessException("Usuário não encontrado.");
        });
        redisClient.deleteValue();
    }

    public void checkRole(Permission permission) {
        AuthContextResponse context = getContext();
        if (!context.role().hasPermission(permission)) {
            log.error("Usuário {} não tem permissão para {}", context.email(), permission);
            throw new ForbiddenException("Usuário não autorizado.");
        }
    }

}
