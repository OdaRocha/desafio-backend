package com.simplesdental.product.validator;

import br.com.fluentvalidator.context.ValidationResult;
import com.simplesdental.product.dto.LoginRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LoginValidatorTest {

    private LoginValidator loginValidator;

    @BeforeEach
    void setup() {
        loginValidator = new LoginValidator();
    }

    @Test
    void deveFalharQuandoEmailForNull() {
        LoginRequest loginRequest = new LoginRequest(null, "senha123");

        ValidationResult result = loginValidator.validate(loginRequest);

        assertFalse(result.isValid());
        assertTrue(result.getErrors().size() > 0);

        var error = result.getErrors().iterator().next();
        assertEquals("Email é obrigatório para login", error.getMessage());
        assertEquals("Email", error.getField());
        assertEquals("BAD_REQUEST", error.getCode());
    }

    @Test
    void deveFalharQuandoEmailMaiorQue50Caracteres() {
        String emailGrande = "a".repeat(45) + "@teste.com"; // Mais de 50 caracteres
        LoginRequest loginRequest = new LoginRequest(emailGrande, "senha123");

        ValidationResult result = loginValidator.validate(loginRequest);

        assertFalse(result.isValid());
        assertTrue(result.getErrors().size() > 0);

        var error = result.getErrors().iterator().next();
        assertEquals("Email deve ter menos que 50 caracteres", error.getMessage());
        assertEquals("Email", error.getField());
        assertEquals("BAD_REQUEST", error.getCode());
    }

    @Test
    void deveFalharQuandoSenhaForNull() {
        LoginRequest loginRequest = new LoginRequest("teste@email.com", null);

        ValidationResult result = loginValidator.validate(loginRequest);

        assertFalse(result.isValid());
        assertTrue(result.getErrors().size() > 0);

        var error = result.getErrors().iterator().next();
        assertEquals("Senha é obrigatória para o login", error.getMessage());
        assertEquals("Password", error.getField());
        assertEquals("BAD_REQUEST", error.getCode());
    }

    @Test
    void deveFalharQuandoSenhaMaiorQue100Caracteres() {
        String senhaGrande = "a".repeat(101);
        LoginRequest loginRequest = new LoginRequest("teste@email.com", senhaGrande);

        ValidationResult result = loginValidator.validate(loginRequest);

        assertFalse(result.isValid());
        assertTrue(result.getErrors().size() > 0);

        var error = result.getErrors().iterator().next();
        assertEquals("Senha deve ter menos que 100 caracteres", error.getMessage());
        assertEquals("Password", error.getField());
        assertEquals("BAD_REQUEST", error.getCode());
    }

    @Test
    void deveFalharQuandoSenhaMenorQue6Caracteres() {
        LoginRequest loginRequest = new LoginRequest("teste@email.com", "12345");

        ValidationResult result = loginValidator.validate(loginRequest);

        assertFalse(result.isValid());
        assertTrue(result.getErrors().size() > 0);

        var error = result.getErrors().iterator().next();
        assertEquals("Senha deve ter mais que 6 caracteres", error.getMessage());
        assertEquals("Password", error.getField());
        assertEquals("BAD_REQUEST", error.getCode());
    }

    @Test
    void devePassarQuandoLoginRequestValida() {
        LoginRequest loginRequest = new LoginRequest("teste@email.com", "senha123");

        ValidationResult result = loginValidator.validate(loginRequest);

        assertTrue(result.isValid());
        assertEquals(0, result.getErrors().size());
    }
}
