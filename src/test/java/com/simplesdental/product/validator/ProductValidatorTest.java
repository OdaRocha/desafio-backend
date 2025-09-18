package com.simplesdental.product.validator;

import br.com.fluentvalidator.context.ValidationResult;
import com.simplesdental.product.dto.ProductV2DTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class ProductValidatorTest {

    private ProductValidator productValidator;

    @BeforeEach
    void setup() {
        productValidator = new ProductValidator();
    }

    @Test
    void deveFalharQuandoNomeForNull() {
        ProductV2DTO productDTO = new ProductV2DTO(null, null, "Descrição", BigDecimal.TEN, true, 1, 1L);

        ValidationResult result = productValidator.validate(productDTO);

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
        ProductV2DTO productDTO = new ProductV2DTO(null, nomeMuitoGrande, "Descrição", BigDecimal.TEN, true, 1, 1L);

        ValidationResult result = productValidator.validate(productDTO);

        assertFalse(result.isValid());
        assertTrue(result.getErrors().size() > 0);

        var error = result.getErrors().iterator().next();
        assertEquals("Nome deve ter menos que 100 caracteres", error.getMessage());
        assertEquals("name", error.getField());
        assertEquals("BAD_REQUEST", error.getCode());
    }

    @Test
    void deveFalharQuandoDescricaoMaiorQue255Caracteres() {
        String descricaoMuitoGrande = "a".repeat(256);
        ProductV2DTO productDTO = new ProductV2DTO(null, "Nome", descricaoMuitoGrande, BigDecimal.TEN, true, 1, 1L);

        ValidationResult result = productValidator.validate(productDTO);

        assertFalse(result.isValid());
        assertTrue(result.getErrors().size() > 0);

        var error = result.getErrors().iterator().next();
        assertEquals("Descrição deve ter menos que 255 caracteres", error.getMessage());
        assertEquals("description", error.getField());
        assertEquals("BAD_REQUEST", error.getCode());
    }

    @Test
    void deveFalharQuandoPrecoForNull() {
        ProductV2DTO productDTO = new ProductV2DTO(null, "Nome", "Descrição", null, true, 1, 1L);

        ValidationResult result = productValidator.validate(productDTO);

        assertFalse(result.isValid());
        assertTrue(result.getErrors().size() > 0);

        var error = result.getErrors().iterator().next();
        assertEquals("Preço é obrigatório", error.getMessage());
        assertEquals("price", error.getField());
        assertEquals("BAD_REQUEST", error.getCode());
    }

    @Test
    void deveFalharQuandoPrecoMenorQueZero() {
        ProductV2DTO productDTO = new ProductV2DTO(null, "Nome", "Descrição", BigDecimal.valueOf(-1), true, 1, 1L);

        ValidationResult result = productValidator.validate(productDTO);

        assertFalse(result.isValid());
        assertTrue(result.getErrors().size() > 0);

        var error = result.getErrors().iterator().next();
        assertEquals("Preço deve ser maior que zero", error.getMessage());
        assertEquals("price", error.getField());
        assertEquals("BAD_REQUEST", error.getCode());
    }

    @Test
    void deveFalharQuandoStatusForNull() {
        ProductV2DTO productDTO = new ProductV2DTO(null, "Nome", "Descrição", BigDecimal.TEN, null, 1, 1L);

        ValidationResult result = productValidator.validate(productDTO);

        assertFalse(result.isValid());
        assertTrue(result.getErrors().size() > 0);

        var error = result.getErrors().iterator().next();
        assertEquals("Status é obrigatório", error.getMessage());
        assertEquals("status", error.getField());
        assertEquals("BAD_REQUEST", error.getCode());
    }

    @Test
    void deveFalharQuandoCategoryIdForNull() {
        ProductV2DTO productDTO = new ProductV2DTO(null, "Nome", "Descrição", BigDecimal.TEN, true, 1, null);

        ValidationResult result = productValidator.validate(productDTO);

        assertFalse(result.isValid());
        assertTrue(result.getErrors().size() > 0);

        var error = result.getErrors().iterator().next();
        assertEquals("Categoria é obrigatória", error.getMessage());
        assertEquals("category", error.getField());
        assertEquals("BAD_REQUEST", error.getCode());
    }

    @Test
    void devePassarQuandoProductDTOValida() {
        ProductV2DTO productDTO = new ProductV2DTO(1L, "Nome Válido", "Descrição válida", BigDecimal.TEN, true, 1, 1L);

        ValidationResult result = productValidator.validate(productDTO);

        assertTrue(result.isValid());
        assertEquals(0, result.getErrors().size());
    }
}
