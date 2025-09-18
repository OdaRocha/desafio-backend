package com.simplesdental.product.service;

import br.com.fluentvalidator.context.ValidationResult;
import com.simplesdental.product.dto.CategoryDTO;
import com.simplesdental.product.dto.ProductDTO;
import com.simplesdental.product.dto.ProductV2DTO;
import com.simplesdental.product.exception.BusinessException;
import com.simplesdental.product.exception.EntityNotFoundException;
import com.simplesdental.product.exception.ValidationException;
import com.simplesdental.product.model.Category;
import com.simplesdental.product.model.Product;
import com.simplesdental.product.repository.ProductRepository;
import com.simplesdental.product.validator.ProductValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductValidator validator;

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private ProductService productService;

    private Product product1;
    private Product product2;

    @BeforeEach
    void setup() {
        Category category = new Category(1L);
        category.setName("Categoria Teste");

        product1 = new Product();
        product1.setId(1L);
        product1.setCode(1);
        product1.setName("Produto 1");
        product1.setDescription("Descrição do Produto 1");
        product1.setPrice(BigDecimal.TEN);
        product1.setStatus(true);
        product1.setCategory(category);

        product2 = new Product();
        product2.setId(2L);
        product2.setCode(2);
        product2.setName("Produto 2");
        product2.setDescription("Descrição do Produto 2");
        product2.setPrice(BigDecimal.TEN);
        product2.setStatus(true);
        product2.setCategory(category);
    }

    @Test
    void deveRetornarPaginaDeProductDTONoFindAll() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> productPage = new PageImpl<>(Arrays.asList(product1, product2));

        when(productRepository.findAll(pageable)).thenReturn(productPage);

        Page<ProductDTO> result = productService.findAll(pageable);

        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals("PROD-1", result.getContent().get(0).code());
        assertEquals("Produto 1", result.getContent().get(0).name());
        assertEquals("PROD-2", result.getContent().get(1).code());
        assertEquals("Produto 2", result.getContent().get(1).name());
        verify(productRepository).findAll(pageable);
    }

    @Test
    void deveRetornarPaginaDeProductV2DTONoFindAllV2() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> productPage = new PageImpl<>(Arrays.asList(product1, product2));

        when(productRepository.findAll(pageable)).thenReturn(productPage);

        Page<ProductV2DTO> result = productService.findAllV2(pageable);

        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals(1, result.getContent().get(0).getCode());
        assertEquals("Produto 1", result.getContent().get(0).getName());
        assertEquals(2, result.getContent().get(1).getCode());
        assertEquals("Produto 2", result.getContent().get(1).getName());
        verify(productRepository).findAll(pageable);
    }

    @Test
    void deveRetornarProductDTOQuandoEncontrarPorId() {
        Long productId = 1L;
        when(productRepository.findById(productId)).thenReturn(Optional.of(product1));

        Optional<ProductDTO> result = productService.findById(productId);

        assertTrue(result.isPresent());
        assertEquals("PROD-1", result.get().code());
        assertEquals("Produto 1", result.get().name());
        assertEquals(BigDecimal.TEN, result.get().price());
        verify(productRepository).findById(productId);
    }

    @Test
    void deveRetornarOptionalVazioQuandoNaoEncontrarPorId() {
        Long productId = 999L;
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        Optional<ProductDTO> result = productService.findById(productId);

        assertFalse(result.isPresent());
        verify(productRepository).findById(productId);
    }

    @Test
    void deveRetornarProductV2DTOQuandoEncontrarPorIdV2() {
        Long productId = 1L;
        when(productRepository.findById(productId)).thenReturn(Optional.of(product1));

        Optional<ProductV2DTO> result = productService.findByIdV2(productId);

        assertTrue(result.isPresent());
        assertEquals(1, result.get().getCode());
        assertEquals("Produto 1", result.get().getName());
        assertEquals(BigDecimal.TEN, result.get().getPrice());
        verify(productRepository).findById(productId);
    }

    @Test
    void deveRetornarOptionalVazioQuandoNaoEncontrarPorIdV2() {
        Long productId = 999L;
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        Optional<ProductV2DTO> result = productService.findByIdV2(productId);

        assertFalse(result.isPresent());
        verify(productRepository).findById(productId);
    }

    @Test
    void deveLancarValidationExceptionQuandoValidacaoFalha() {
        ProductV2DTO productDTO = new ProductV2DTO(1L, "NOME INVALIDO", null, BigDecimal.TEN, true,1, 1L);

        ValidationResult validationResult = mock(ValidationResult.class);
        when(validationResult.isValid()).thenReturn(false);
        when(validator.validate(productDTO)).thenReturn(validationResult);

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            productService.save(productDTO);
        });

        verify(validator).validate(productDTO);
        verify(categoryService, never()).findById(any());
        verify(productRepository, never()).save(any());
    }

    @Test
    void deveLancarEntityNotFoundExceptionQuandoCategoriaNaoEncontrada() {
        ProductV2DTO productDTO = new ProductV2DTO(1L, "Produto Teste", null, BigDecimal.TEN, true,1, 999L);

        ValidationResult validationResult = mock(ValidationResult.class);
        when(validationResult.isValid()).thenReturn(true);
        when(validator.validate(productDTO)).thenReturn(validationResult);
        when(categoryService.findById(999L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            productService.save(productDTO);
        });

        assertEquals("Categoria não encontrada", exception.getMessage());
        verify(validator).validate(productDTO);
        verify(categoryService).findById(999L);
        verify(productRepository, never()).save(any());
    }

    @Test
    void deveSalvarProdutoComSucesso() {
        ProductV2DTO productDTO = new ProductV2DTO(1L, "Produto Teste", "Descrição teste", BigDecimal.TEN, true,1, 1L);

        CategoryDTO categoryDTO = new CategoryDTO(1L, "Categoria Teste", "Descrição teste");

        ValidationResult validationResult = mock(ValidationResult.class);
        when(validationResult.isValid()).thenReturn(true);
        when(validator.validate(productDTO)).thenReturn(validationResult);
        when(categoryService.findById(1L)).thenReturn(Optional.of(categoryDTO));

        Product savedProduct = new Product();
        savedProduct.setId(1L);
        savedProduct.setCode(1);
        savedProduct.setName("Produto Teste");
        savedProduct.setDescription("Descrição teste");
        savedProduct.setPrice(BigDecimal.TEN);
        savedProduct.setStatus(true);

        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        ProductV2DTO result = productService.save(productDTO);

        assertNotNull(result);
        assertEquals(1, result.getCode());
        assertEquals("Produto Teste", result.getName());
        assertEquals("Descrição teste", result.getDescription());
        assertEquals(BigDecimal.TEN, result.getPrice());
        assertTrue(result.getStatus());

        verify(validator).validate(productDTO);
        verify(categoryService).findById(1L);
        verify(productRepository).save(any(Product.class));
    }
    @Test
    void deveLancarBusinessExceptionQuandoProdutoNaoEncontradoNoDelete() {
        Long productId = 999L;
        when(productRepository.deleteProductById(productId)).thenReturn(0);

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            productService.deleteById(productId);
        });

        assertEquals("Produto não encontrado", exception.getMessage());
        verify(productRepository).deleteProductById(productId);
    }

    @Test
    void deveDeletarProdutoComSucesso() {
        Long productId = 1L;
        when(productRepository.deleteProductById(productId)).thenReturn(1);

        assertDoesNotThrow(() -> {
            productService.deleteById(productId);
        });

        verify(productRepository).deleteProductById(productId);
    }

    @Test
    void deveLancarValidationExceptionQuandoValidacaoFalhaNoUpdate() {
        Long productId = 1L;
        ProductV2DTO productDTO = new ProductV2DTO(1L, "NOME INVALIDO", null, BigDecimal.TEN, true, 1, 1L);

        ValidationResult validationResult = mock(ValidationResult.class);
        when(validationResult.isValid()).thenReturn(false);
        when(validator.validate(productDTO)).thenReturn(validationResult);

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            productService.updateProduct(productId, productDTO);
        });

        verify(validator).validate(productDTO);
        verify(categoryService, never()).findById(any());
        verify(productRepository, never()).findById(any());
        verify(productRepository, never()).save(any());
    }

    @Test
    void deveLancarEntityNotFoundExceptionQuandoCategoriaNaoEncontradaNoUpdate() {
        Long productId = 1L;
        ProductV2DTO productDTO = new ProductV2DTO(1L, "Produto Teste", "Descrição", BigDecimal.TEN, true, 1, 999L);

        ValidationResult validationResult = mock(ValidationResult.class);
        when(validationResult.isValid()).thenReturn(true);
        when(validator.validate(productDTO)).thenReturn(validationResult);
        when(categoryService.findById(999L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            productService.updateProduct(productId, productDTO);
        });

        assertEquals("Categoria não encontrada", exception.getMessage());
        verify(validator).validate(productDTO);
        verify(categoryService).findById(999L);
        verify(productRepository, never()).findById(any());
        verify(productRepository, never()).save(any());
    }

    @Test
    void deveLancarBusinessExceptionQuandoProdutoNaoEncontradoNoUpdate() {
        Long productId = 999L;
        ProductV2DTO productDTO = new ProductV2DTO(1L, "Produto Teste", "Descrição", BigDecimal.TEN, true, 1, 1L);

        CategoryDTO categoryDTO = new CategoryDTO(1L, "Categoria Teste", "Descrição teste");

        ValidationResult validationResult = mock(ValidationResult.class);
        when(validationResult.isValid()).thenReturn(true);
        when(validator.validate(productDTO)).thenReturn(validationResult);
        when(categoryService.findById(1L)).thenReturn(Optional.of(categoryDTO));
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            productService.updateProduct(productId, productDTO);
        });

        assertEquals("Produto não encontrado", exception.getMessage());
        verify(validator).validate(productDTO);
        verify(categoryService).findById(1L);
        verify(productRepository).findById(productId);
        verify(productRepository, never()).save(any());
    }

    @Test
    void deveAtualizarProdutoComSucesso() {
        Long productId = 1L;
        ProductV2DTO productDTO = new ProductV2DTO(1L, "Produto Atualizado", "Nova descrição", new BigDecimal("15.00"), false, 2, 2L);

        CategoryDTO categoryDTO = new CategoryDTO(2L, "Nova Categoria", "Descrição categoria");

        ValidationResult validationResult = mock(ValidationResult.class);
        when(validationResult.isValid()).thenReturn(true);
        when(validator.validate(productDTO)).thenReturn(validationResult);
        when(categoryService.findById(2L)).thenReturn(Optional.of(categoryDTO));
        when(productRepository.findById(productId)).thenReturn(Optional.of(product1));

        Product updatedProduct = new Product();
        updatedProduct.setId(productId);
        updatedProduct.setCode(2);
        updatedProduct.setName("Produto Atualizado");
        updatedProduct.setDescription("Nova descrição");
        updatedProduct.setPrice(new BigDecimal("15.00"));
        updatedProduct.setStatus(false);

        when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);

        ProductV2DTO result = productService.updateProduct(productId, productDTO);

        assertNotNull(result);
        assertEquals(2, result.getCode());
        assertEquals("Produto Atualizado", result.getName());
        assertEquals("Nova descrição", result.getDescription());
        assertEquals(new BigDecimal("15.00"), result.getPrice());
        assertFalse(result.getStatus());

        verify(validator).validate(productDTO);
        verify(categoryService).findById(2L);
        verify(productRepository).findById(productId);
        verify(productRepository).save(any(Product.class));
    }


}
