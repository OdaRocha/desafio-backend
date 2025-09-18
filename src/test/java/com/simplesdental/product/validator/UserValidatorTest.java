package com.simplesdental.product.validator;

import br.com.fluentvalidator.context.ValidationResult;
import com.simplesdental.product.dto.Role;
import com.simplesdental.product.dto.UserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserValidatorTest {

    private UserValidator userValidator;

    @BeforeEach
    void setup() {
        userValidator = new UserValidator();
    }

    @Test
    void deveFalharQuandoNomeObrigatorioNaoInformado() {
        UserDTO userDTO = new UserDTO(null, "password123", null, "test@email.com", Role.USER);

        ValidationResult result = userValidator.validate(userDTO);

        assertFalse(result.isValid());
        assertTrue(result.getErrors().size() > 0);

        var error = result.getErrors().iterator().next();
        assertEquals("Nome é obrigatório", error.getMessage());
        assertEquals("name", error.getField());
        assertEquals("BAD_REQUEST", error.getCode());
    }

    @Test
    void deveFalharQuandoNomeMaiorQue100Caracteres() {
        String nomeMuitoGrande = "a".repeat(101);
        UserDTO userDTO = new UserDTO(null, "password123", nomeMuitoGrande, "test@email.com", Role.USER);

        ValidationResult result = userValidator.validate(userDTO);

        assertFalse(result.isValid());
        assertTrue(result.getErrors().size() > 0);

        var error = result.getErrors().iterator().next();
        assertEquals("Nome deve ter menos que 100 caracteres", error.getMessage());
        assertEquals("name", error.getField());
        assertEquals("BAD_REQUEST", error.getCode());
    }

    @Test
    void deveFalharQuandoEmailForNull() {
        UserDTO userDTO = new UserDTO(null, "password123", "Nome Válido", null, Role.USER);

        ValidationResult result = userValidator.validate(userDTO);

        assertFalse(result.isValid());
        assertTrue(result.getErrors().size() > 0);

        var error = result.getErrors().iterator().next();
        assertEquals("Email é obrigatório", error.getMessage());
        assertEquals("email", error.getField());
        assertEquals("BAD_REQUEST", error.getCode());
    }

    @Test
    void deveFalharQuandoEmailMaiorQue50Caracteres() {
        String emailGrande = "a".repeat(45) + "@teste.com";
        UserDTO userDTO = new UserDTO(null, "password123", "Nome Válido", emailGrande, Role.USER);

        ValidationResult result = userValidator.validate(userDTO);

        assertFalse(result.isValid());
        assertTrue(result.getErrors().size() > 0);

        var error = result.getErrors().iterator().next();
        assertEquals("Email deve ter menos que 50 caracteres", error.getMessage());
        assertEquals("email", error.getField());
    }

    @Test
    void deveFalharQuandoEmailInvalidoNaRegex() {
        UserDTO userDTO = new UserDTO(null, "Nome Válido", "email-invalido", "password123", Role.USER);

        ValidationResult result = userValidator.validate(userDTO);

        assertFalse(result.isValid());
        assertTrue(result.getErrors().size() > 0);

        var error = result.getErrors().iterator().next();
        assertEquals("Email inválido", error.getMessage());
        assertEquals("email", error.getField());
        assertEquals("BAD_REQUEST", error.getCode());
    }

    @Test
    void deveFalharQuandoPasswordForNull() {
        UserDTO userDTO = new UserDTO(null, null, "Nome Válido", "test@email.com", Role.USER);

        ValidationResult result = userValidator.validate(userDTO);

        assertFalse(result.isValid());
        assertTrue(result.getErrors().size() > 0);

        var error = result.getErrors().iterator().next();
        assertEquals("Password é obrigatório", error.getMessage());
        assertEquals("password", error.getField());
        assertEquals("BAD_REQUEST", error.getCode());
    }

    @Test
    void deveFalharQuandoPasswordMaiorQue100Caracteres() {
        String passwordGrande = "a".repeat(101);
        UserDTO userDTO = new UserDTO(null, passwordGrande, "Nome Válido", "test@email.com", Role.USER);

        ValidationResult result = userValidator.validate(userDTO);

        assertFalse(result.isValid());
        assertTrue(result.getErrors().size() > 0);

        var error = result.getErrors().iterator().next();
        assertEquals("Password deve ter menos que 100 caracteres", error.getMessage());
        assertEquals("password", error.getField());
        assertEquals("BAD_REQUEST", error.getCode());
    }

    @Test
    void deveFalharQuandoRoleObrigatorioNaoInformado() {
        UserDTO userDTO = new UserDTO(null, "password123", "Nome Válido", "test@email.com", null);

        ValidationResult result = userValidator.validate(userDTO);

        assertFalse(result.isValid());
        assertTrue(result.getErrors().size() > 0);

        var error = result.getErrors().iterator().next();
        assertEquals("Role é obrigatório", error.getMessage());
        assertEquals("role", error.getField());
        assertEquals("BAD_REQUEST", error.getCode());
    }

    @Test
    void devePassarQuandoUserDTOValida() {
        UserDTO userDTO = new UserDTO(1L, "password123", "Nome Válido", "test@email.com", Role.USER);

        ValidationResult result = userValidator.validate(userDTO);

        assertTrue(result.isValid());
        assertEquals(0, result.getErrors().size());
    }
}
