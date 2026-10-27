package com.berkay.personalfinance.mapper;

import com.berkay.personalfinance.dto.CategoryRequestDto;
import com.berkay.personalfinance.dto.CategoryResponseDto;
import com.berkay.personalfinance.entity.Category;
import com.berkay.personalfinance.entity.User;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    // Request DTO + Oturum Açan User -> Entity (Veritabanına kaydetmek için)
    public Category toEntity(CategoryRequestDto categoryRequestDto, User user) {
        if (categoryRequestDto == null) {
            return null;
        }
        return Category.builder()
                .name(categoryRequestDto.getName().trim())
                .type(categoryRequestDto.getCategoryType())
                .user(user)
                .build();

    }

    // Entity -> Response DTO (İstemciye JSON dönmek için)
    public CategoryResponseDto toResponseDto(Category category) {
        if (category == null) {
            return null;
        }
    return CategoryResponseDto.builder()
                .id(category.getId())
            .name(category.getName())
            .categoryType(category.getType())
            .createdAt(category.getCreatedAt())
            .updatedAt(category.getUpdatedAt())
            .build();

    }
}
