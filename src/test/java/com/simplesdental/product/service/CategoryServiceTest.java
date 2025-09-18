package com.simplesdental.product.service;

import br.com.fluentvalidator.context.ValidationResult;
import com.simplesdental.product.dto.CategoryDTO;
import com.simplesdental.product.exception.BusinessException;
import com.simplesdental.product.exception.ValidationException;
import com.simplesdental.product.model.Category;
import com.simplesdental.product.repository.CategoryRepository;
import com.simplesdental.product.validator.CategoryValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryValidator validator;

    @InjectMocks
    private CategoryService categoryService;

    private Category categoria1;
    private List<Category> listaCategorias;

    @BeforeEach
    void setUp() {
        categoria1 = new Category(1L, "Eletrônicos", "Produtos eletrônicos", Collections.emptyList());

        listaCategorias = List.of(categoria1);
    }

    @Test
    void deveRetornarTodasCategoriasComPaginacao() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Category> paginaCategorias = new PageImpl<>(listaCategorias, pageable, listaCategorias.size());

        when(categoryRepository.findAll(pageable)).thenReturn(paginaCategorias);

        Page<CategoryDTO> resultado = categoryService.findAll(pageable);

        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
        assertEquals("Eletrônicos", resultado.getContent().get(0).name());
        verify(categoryRepository).findAll(pageable);
    }

    @Test
    void deveRetornarCategoriaQuandoIdExiste() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(categoria1));

        Optional<CategoryDTO> resultado = categoryService.findById(1L);

        assertTrue(resultado.isPresent());
        assertEquals("Eletrônicos", resultado.get().name());
        assertEquals("Produtos eletrônicos", resultado.get().description());
        verify(categoryRepository).findById(1L);
    }

    @Test
    void deveRetornarOptionalVazioQuandoIdNaoExiste() {
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<CategoryDTO> resultado = categoryService.findById(99L);

        assertFalse(resultado.isPresent());
        verify(categoryRepository).findById(99L);
    }


    @Test
    void deveLancarExcecaoQuandoValidacaoFalharAoSalvar() {
        CategoryDTO categoryDTO = new CategoryDTO(null, "Computadores", "Equipamentos de informática");

        ValidationResult validationResult = mock(ValidationResult.class);
        when(validationResult.isValid()).thenReturn(false);
        when(validator.validate(categoryDTO)).thenReturn(validationResult);

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            categoryService.save(categoryDTO);
        });

        verify(validator).validate(categoryDTO);
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void deveSalvarCategoriaQuandoValidacaoPassa() {
        CategoryDTO categoryDTO = new CategoryDTO(null, "Computadores", "Equipamentos de informática");

        ValidationResult validationResult = mock(ValidationResult.class);
        when(validationResult.isValid()).thenReturn(true);
        when(validator.validate(categoryDTO)).thenReturn(validationResult);

        Category savedCategory = new Category(3L, "Computadores", "Equipamentos de informática", Collections.emptyList());

        ArgumentCaptor<Category> categoryCaptor = ArgumentCaptor.forClass(Category.class);
        when(categoryRepository.save(categoryCaptor.capture())).thenReturn(savedCategory);

        CategoryDTO result = categoryService.save(categoryDTO);

        assertNotNull(result);
        assertEquals(3L, result.id());
        assertEquals("Computadores", result.name());
        assertEquals("Equipamentos de informática", result.description());

        Category capturedCategory = categoryCaptor.getValue();
        assertEquals("Computadores", capturedCategory.getName());
        assertEquals("Equipamentos de informática", capturedCategory.getDescription());

        verify(validator).validate(categoryDTO);
        verify(categoryRepository).save(any(Category.class));
    }
    @Test
    void deveLancarExcecaoQuandoValidacaoFalharAoAtualizar() {
        Long id = 1L;
        CategoryDTO categoryDTO = new CategoryDTO(id, "Computadores", "");

        ValidationResult validationResult = mock(ValidationResult.class);
        when(validationResult.isValid()).thenReturn(false);
        when(validator.validate(categoryDTO)).thenReturn(validationResult);

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            categoryService.update(id, categoryDTO);
        });

        verify(validator).validate(categoryDTO);
        verify(categoryRepository, never()).findById(any());
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void deveLancarBusinessExceptionQuandoCategoriaNaoExisteAoAtualizar() {
        Long id = 99L;
        CategoryDTO categoryDTO = new CategoryDTO(id, "Computadores", "Equipamentos de informática");

        ValidationResult validationResult = mock(ValidationResult.class);
        when(validationResult.isValid()).thenReturn(true);
        when(validator.validate(categoryDTO)).thenReturn(validationResult);

        when(categoryRepository.findById(id)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            categoryService.update(id, categoryDTO);
        });

        assertEquals("Categoria não encontrada", exception.getMessage());
        verify(validator).validate(categoryDTO);
        verify(categoryRepository).findById(id);
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void deveAtualizarCategoriaQuandoValidacaoPassaECategoriaExiste() {
        Long id = 1L;
        CategoryDTO categoryDTO = new CategoryDTO(id, "Computadores Atualizados", "Equipamentos de informática atualizados");

        ValidationResult validationResult = mock(ValidationResult.class);
        when(validationResult.isValid()).thenReturn(true);
        when(validator.validate(categoryDTO)).thenReturn(validationResult);

        Category existingCategory = new Category(id, "Computadores", "Equipamentos de informática", Collections.emptyList());
        Category updatedCategory = new Category(id, "Computadores Atualizados", "Equipamentos de informática atualizados", Collections.emptyList());

        when(categoryRepository.findById(id)).thenReturn(Optional.of(existingCategory));
        when(categoryRepository.save(any(Category.class))).thenReturn(updatedCategory);

        CategoryDTO result = categoryService.update(id, categoryDTO);

        assertNotNull(result);
        assertEquals(id, result.id());
        assertEquals("Computadores Atualizados", result.name());
        assertEquals("Equipamentos de informática atualizados", result.description());

        verify(validator).validate(categoryDTO);
        verify(categoryRepository).findById(id);
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void deveLancarBusinessExceptionQuandoExistemProdutosNaCategoria() {
        Long id = 1L;
        when(categoryRepository.hasProductsInCategory(id)).thenReturn(true);

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            categoryService.deleteById(id);
        });

        assertEquals("Não é possível excluir a categoria pois existem produtos vinculados", exception.getMessage());
        verify(categoryRepository).hasProductsInCategory(id);
        verify(categoryRepository, never()).deleteCategoryById(any());
    }

    @Test
    void deveLancarBusinessExceptionQuandoCategoriaNaoExisteAoExcluir() {
        Long id = 99L;
        when(categoryRepository.hasProductsInCategory(id)).thenReturn(false);
        when(categoryRepository.deleteCategoryById(id)).thenReturn(0);

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            categoryService.deleteById(id);
        });

        assertEquals("Categoria não encontrado", exception.getMessage());
        verify(categoryRepository).hasProductsInCategory(id);
        verify(categoryRepository).deleteCategoryById(id);
    }

    @Test
    void deveExcluirCategoriaQuandoNaoExistemProdutosVinculados() {
        Long id = 1L;
        when(categoryRepository.hasProductsInCategory(id)).thenReturn(false);
        when(categoryRepository.deleteCategoryById(id)).thenReturn(1);

        categoryService.deleteById(id);

        verify(categoryRepository).hasProductsInCategory(id);
        verify(categoryRepository).deleteCategoryById(id);
    }


}
