package com.berkay.personalfinance.service;


import com.berkay.personalfinance.dto.CategoryRequestDto;
import com.berkay.personalfinance.dto.CategoryResponseDto;
import com.berkay.personalfinance.enums.CategoryType;

import java.util.List;

public interface CategoryService {
    CategoryResponseDto createCategory(CategoryRequestDto requestDto, Long userId);

    List<CategoryResponseDto> getCategoriesByUser(Long userId, CategoryType type);

    CategoryResponseDto getCategoryById(Long id, Long userId);

    CategoryResponseDto updateCategory(Long id, CategoryRequestDto requestDto, Long userId);

    void deleteCategory(Long id, Long userId);

}
