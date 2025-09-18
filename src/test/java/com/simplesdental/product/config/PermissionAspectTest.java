package com.simplesdental.product.config;

import com.simplesdental.product.dto.Permission;
import com.simplesdental.product.exception.ForbiddenException;
import com.simplesdental.product.service.AuthService;
import org.aspectj.lang.JoinPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PermissionAspectTest {

    @Mock
    private AuthService authService;

    @Mock
    private JoinPoint joinPoint;

    @Mock
    private CheckPermission checkPermission;

    @InjectMocks
    private PermissionAspect permissionAspect;

    @BeforeEach
    void setup() {
        when(checkPermission.value()).thenReturn(Permission.CREATE_PRODUCT);
    }

    @Test
    void devePassarQuandoPermissaoValida() {
        doNothing().when(authService).checkRole(Permission.CREATE_PRODUCT);

        assertDoesNotThrow(() -> permissionAspect.check(joinPoint, checkPermission));

        verify(authService, times(1)).checkRole(Permission.CREATE_PRODUCT);
        verify(checkPermission, times(1)).value();
    }

    @Test
    void deveFalharQuandoPermissaoNegada() {
        ForbiddenException forbidden = new ForbiddenException("Acesso negado");
        doThrow(forbidden).when(authService).checkRole(Permission.CREATE_PRODUCT);

        ForbiddenException exception = assertThrows(ForbiddenException.class,
                () -> permissionAspect.check(joinPoint, checkPermission));

        assertEquals("Acesso negado", exception.getMessage());

        verify(authService, times(1)).checkRole(Permission.CREATE_PRODUCT);
        verify(checkPermission, times(1)).value();
    }

    @Test
    void deveFalharQuandoOcorreErroInesperado() {
        RuntimeException runtimeException = new RuntimeException("Erro interno");
        doThrow(runtimeException).when(authService).checkRole(Permission.CREATE_PRODUCT);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> permissionAspect.check(joinPoint, checkPermission));

        assertEquals("Erro interno", exception.getMessage());

        verify(authService, times(1)).checkRole(Permission.CREATE_PRODUCT);
        verify(checkPermission, times(1)).value();
    }
}
