package com.simplesdental.product.validator;

import br.com.fluentvalidator.context.ValidationResult;
import com.simplesdental.product.dto.CategoryDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CategoryValidatorTest {

    private CategoryValidator categoryValidator;

    @BeforeEach
    void setup() {
        categoryValidator = new CategoryValidator();
    }


    @Test
    void deveFalharQuandoNomeObrigatorioNaoInformado() {
        CategoryDTO categoryDTO = new CategoryDTO(null, null, "Descrição válida");

        ValidationResult result = categoryValidator.validate(categoryDTO);

        assertFalse(result.isValid());
        assertEquals(1, result.getErrors().size());

        var error = result.getErrors().iterator().next();
        assertEquals("Nome é obrigatório", error.getMessage());
        assertEquals("name", error.getField());
        assertEquals("BAD_REQUEST", error.getCode());
    }

    @Test
    void deveFalharQuandoNomeVazio() {
        CategoryDTO categoryDTO = new CategoryDTO(null, "", "Descrição válida");

        ValidationResult result = categoryValidator.validate(categoryDTO);

        assertFalse(result.isValid());
        assertEquals(1, result.getErrors().size());

        var error = result.getErrors().iterator().next();
        assertEquals("Nome é obrigatório", error.getMessage());
    }

    @Test
    void deveFalharQuandoNomeMaiorQue100Caracteres() {
        String nomeMuitoGrande = "a".repeat(101);
        CategoryDTO categoryDTO = new CategoryDTO(null, nomeMuitoGrande, "Descrição válida");

        ValidationResult result = categoryValidator.validate(categoryDTO);

        assertFalse(result.isValid());
        assertEquals(1, result.getErrors().size());

        var error = result.getErrors().iterator().next();
        assertEquals("Nome deve ter menos que 100 caracteres", error.getMessage());
        assertEquals("name", error.getField());
        assertEquals("BAD_REQUEST", error.getCode());
    }

    @Test
    void deveFalharQuandoDescricaoMaiorQue255Caracteres() {
        String descricaoMuitoGrande = "a".repeat(256);
        CategoryDTO categoryDTO = new CategoryDTO(null, "Nome válido", descricaoMuitoGrande);

        ValidationResult result = categoryValidator.validate(categoryDTO);

        assertFalse(result.isValid());
        assertEquals(1, result.getErrors().size());

        var error = result.getErrors().iterator().next();
        assertEquals("Descrição deve ter menos que 255 caracteres", error.getMessage());
        assertEquals("description", error.getField());
        assertEquals("BAD_REQUEST", error.getCode());
    }

}
