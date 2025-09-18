package com.simplesdental.product.service;

import br.com.fluentvalidator.context.ValidationResult;
import com.simplesdental.product.config.CheckPermission;
import com.simplesdental.product.dto.CategoryDTO;
import com.simplesdental.product.dto.Permission;
import com.simplesdental.product.exception.BusinessException;
import com.simplesdental.product.exception.ValidationException;
import com.simplesdental.product.mapper.CategoryMapper;
import com.simplesdental.product.model.Category;
import com.simplesdental.product.repository.CategoryRepository;
import com.simplesdental.product.validator.CategoryValidator;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryValidator validator;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository, CategoryValidator validator) {
        this.categoryRepository = categoryRepository;
        this.validator = validator;
    }

    @CheckPermission(Permission.VIEW_CATEGORY)
    public Page<CategoryDTO> findAll(Pageable pageable) {
        log.info("Buscando todas as categorias com paginação: {}", pageable.getPageNumber());
        return categoryRepository.findAll(pageable).map(CategoryMapper::toDTO);
    }

    @CheckPermission(Permission.VIEW_CATEGORY)
    public Optional<CategoryDTO> findById(Long id) {
        log.info("Buscando categoria por id: {}", id);
        Optional<Category> catOptional = categoryRepository.findById(id);
        if (catOptional.isEmpty()) {
            log.warn("Categoria não encontrada para o id: {}", id);
        }
        return catOptional.map(CategoryMapper::toDTO);
    }

    @CheckPermission(Permission.CREATE_CATEGORY)
    public CategoryDTO save(CategoryDTO categoryDTO) {
        log.info("Salvando nova categoria: {}", categoryDTO);

        ValidationResult validation = validator.validate(categoryDTO);

        if (!validation.isValid()) {
            log.error("Falha na validação ao salvar categoria: {}", validation.getErrors());
            throw new ValidationException(validation);
        }

        Category entity = CategoryMapper.toEntity(categoryDTO);

        CategoryDTO saved = CategoryMapper.toDTO(categoryRepository.save(entity));
        log.info("Categoria salva com sucesso: {}", saved);
        return saved;
    }

    @CheckPermission(Permission.UPDATE_CATEGORY)
    public CategoryDTO update(Long id, CategoryDTO categoryDTO) {
        log.info("Atualizando categoria id: {} com dados: {}", id, categoryDTO);

        ValidationResult validation = validator.validate(categoryDTO);

        if (!validation.isValid()) {
            log.error("Falha na validação ao atualizar categoria: {}", validation.getErrors());
            throw new ValidationException(validation);
        }

        return categoryRepository.findById(id)
                .map(existingCategory -> {
                    existingCategory.setName(categoryDTO.name());
                    existingCategory.setDescription(categoryDTO.description());
                    Category updatedCategory = categoryRepository.save(existingCategory);

                    log.info("Categoria atualizada com sucesso: {}", updatedCategory);
                    return CategoryMapper.toDTO(updatedCategory);
                })
                .orElseThrow(() -> {

                    log.error("Categoria não encontrada para atualização, id: {}", id);
                    return new BusinessException("Categoria não encontrada");
                });
    }

    @Transactional
    @CheckPermission(Permission.DELETE_CATEGORY)
    public void deleteById(Long id) {
        log.info("Deletando categoria por id: {}", id);

        if (categoryRepository.hasProductsInCategory(id)) {
            log.error("Não é possível excluir a categoria {} pois existem produtos vinculados", id);
            throw new BusinessException("Não é possível excluir a categoria pois existem produtos vinculados");
        }

        int rowsDeleted = categoryRepository.deleteCategoryById(id);
        if (rowsDeleted == 0) {
            log.error("Categoria não encontrada para deleção, id: {}", id);
            throw new BusinessException("Categoria não encontrado");
        }
        log.info("Categoria deletada com sucesso, id: {}", id);
    }
}
