package com.simplesdental.product.config;

import com.simplesdental.product.dto.Permission;
import com.simplesdental.product.service.AuthService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Responsável pela verificação de permissões dos usuários.
 * Intercepta chamadas a métodos anotados com {@link CheckPermission} e
 * verifica se o usuário atual possui a permissão necessária.
 */
@Aspect
@Component
public class PermissionAspect {

    private static final Logger logger = LoggerFactory.getLogger(PermissionAspect.class);

    private final AuthService authService;

    @Autowired
    public PermissionAspect(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Método executado antes da chamada aos métodos anotados com {@link CheckPermission}.
     * Verifica se o usuário possui a permissão necessária para acessar o recurso.
     *
     * @param joinPoint ponto de junção que representa o método interceptado
     * @param checkPermission anotação contendo a permissão necessária
     */
    @Before("@annotation(checkPermission)")
    public void check(JoinPoint joinPoint, CheckPermission checkPermission) {
        Permission value = checkPermission.value();
        try {
            authService.checkRole(value);
            logger.debug("Permissão {} verificada com sucesso", value);
        } catch (Exception e) {
            logger.error("Erro ao verificar permissão {}: {}", value, e.getMessage());
            throw e;
        }
    }

}
