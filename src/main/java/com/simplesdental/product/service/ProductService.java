package com.simplesdental.product.service;

import br.com.fluentvalidator.context.ValidationResult;
import com.simplesdental.product.config.CheckPermission;
import com.simplesdental.product.dto.CategoryDTO;
import com.simplesdental.product.dto.Permission;
import com.simplesdental.product.dto.ProductDTO;
import com.simplesdental.product.dto.ProductV2DTO;
import com.simplesdental.product.exception.BusinessException;
import com.simplesdental.product.exception.EntityNotFoundException;
import com.simplesdental.product.exception.ValidationException;
import com.simplesdental.product.mapper.ProductMapper;
import com.simplesdental.product.model.Category;
import com.simplesdental.product.model.Product;
import com.simplesdental.product.repository.ProductRepository;
import com.simplesdental.product.validator.ProductValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductValidator validator;
    private final CategoryService categoryService;


    @Autowired
    public ProductService(ProductRepository productRepository, ProductValidator validator, CategoryService categoryService) {
        this.productRepository = productRepository;
        this.validator = validator;
        this.categoryService = categoryService;
    }

    @CheckPermission(Permission.VIEW_PRODUCT)
    public Page<ProductDTO> findAll(Pageable page) {
        log.info("Buscando todos os produtos com paginação: {}", page);
        return productRepository.findAll(page)
                .map(ProductMapper::toDTO);
    }

    @CheckPermission(Permission.VIEW_PRODUCT)
    public Page<ProductV2DTO> findAllV2(Pageable page) {
        log.info("Buscando todos os produtos (V2) com paginação: {}", page);
        return productRepository.findAll(page).map(ProductMapper::toDTOV2);
    }

    @CheckPermission(Permission.VIEW_PRODUCT)
    public Optional<ProductDTO> findById(Long id) {
        log.info("Buscando produto por id: {}", id);

        return productRepository.findById(id).map(ProductMapper::toDTO);
    }

    @CheckPermission(Permission.VIEW_PRODUCT)
    public Optional<ProductV2DTO> findByIdV2(Long id) {
        log.info("Buscando produto (V2) por id: {}", id);

        return productRepository.findById(id).map(ProductMapper::toDTOV2);
    }

    @CheckPermission(Permission.CREATE_PRODUCT)
    public ProductV2DTO save(ProductV2DTO dto) {
        log.info("Salvando novo produto: {}", dto);
        ValidationResult validation = validator.validate(dto);

        if (!validation.isValid()) {
            log.error("Falha na validação ao salvar produto: {}", validation.getErrors());
            throw new ValidationException(validation);
        }

        CategoryDTO categoryDTO = categoryService.findById(dto.getCategoryId())
                .orElseThrow(() -> {
                    log.error("Categoria não encontrada para id: {}", dto.getCategoryId());
                    return new EntityNotFoundException("Categoria não encontrada");
                });

        dto.setCategoryId(categoryDTO.id());
        Product entity = ProductMapper.toEntityV2(dto);
        ProductV2DTO saved = ProductMapper.toDTOV2(productRepository.save(entity));
        log.info("Produto salvo com sucesso: {}", saved);

        return saved;
    }

    @CheckPermission(Permission.UPDATE_PRODUCT)
    public ProductV2DTO updateProduct(Long id, ProductV2DTO productDTO) {
        log.info("Atualizando produto id: {} com dados: {}", id, productDTO);

        ValidationResult validation = validator.validate(productDTO);
        if (!validation.isValid()) {
            log.error("Falha na validação ao atualizar produto: {}", validation.getErrors());
            throw new ValidationException(validation);
        }

        CategoryDTO categoryDTO = categoryService.findById(productDTO.getCategoryId())
                .orElseThrow(() -> {
                    log.error("Categoria não encontrada para id: {}", productDTO.getCategoryId());
                    return new EntityNotFoundException("Categoria não encontrada");
                });

        Category category = new Category(categoryDTO.id());

        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Produto não encontrado para atualização, id: {}", id);
                    return new BusinessException("Produto não encontrado");
                });

        existingProduct.setCode(productDTO.getCode());
        existingProduct.setName(productDTO.getName());
        existingProduct.setDescription(productDTO.getDescription());
        existingProduct.setPrice(productDTO.getPrice());
        existingProduct.setStatus(productDTO.getStatus());
        existingProduct.setCategory(category);
        log.debug("Categoria atualizada para id: {}", productDTO.getCategoryId());


        Product updated = productRepository.save(existingProduct);
        ProductV2DTO result = ProductMapper.toDTOV2(updated);

        log.info("Produto atualizado com sucesso: {}", result);
        return result;
    }

    @CheckPermission(Permission.DELETE_PRODUCT)
    public void deleteById(Long id) {
        log.info("Deletando produto por id: {}", id);
        int rowsDeleted = productRepository.deleteProductById(id);
        if (rowsDeleted == 0) {
            log.error("Produto não encontrado para deleção, id: {}", id);
            throw new BusinessException("Produto não encontrado");
        }
        log.info("Produto deletado com sucesso, id: {}", id);
    }

}
