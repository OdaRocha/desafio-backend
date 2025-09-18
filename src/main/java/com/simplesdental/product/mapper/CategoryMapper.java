package com.simplesdental.product.mapper;

import com.simplesdental.product.dto.CategoryDTO;
import com.simplesdental.product.model.Category;

public class CategoryMapper {
    private CategoryMapper() {
    }

    public static CategoryDTO toDTO(Category category) {
        if (category == null) return null;
        return new CategoryDTO(
                category.getId(),
                category.getName(),
                category.getDescription()
        );
    }

    public static Category toEntity(CategoryDTO dto) {
        if (dto == null) return null;
        Category category = new Category();
        category.setId(dto.id());
        category.setName(dto.name());
        category.setDescription(dto.description());
        return category;
    }
}
