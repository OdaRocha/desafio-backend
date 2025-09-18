package com.simplesdental.product.mapper;

import com.simplesdental.product.dto.ProductDTO;
import com.simplesdental.product.dto.ProductV2DTO;
import com.simplesdental.product.model.Category;
import com.simplesdental.product.model.Product;

public class ProductMapper {
    private ProductMapper() {
    }

    public static ProductDTO toDTO(Product product) {
        if (product == null) return null;
        return ProductDTO.of(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStatus(),
                product.getCode(),
                product.getCategoryId()
        );
    }

    public static Product toEntityV2(ProductV2DTO dto) {
        if (dto == null) return null;
        Product product = new Product();
        product.setId(dto.getId());
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setStatus(dto.getStatus());
        product.setCode(dto.getCode());
        product.setCategory(new Category(dto.getCategoryId()));

        return product;
    }

    public static ProductV2DTO toDTOV2(Product product) {
        if (product == null) return null;
        return new ProductV2DTO(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStatus(),
                product.getCode(),
                product.getCategoryId()
        );
    }
}
