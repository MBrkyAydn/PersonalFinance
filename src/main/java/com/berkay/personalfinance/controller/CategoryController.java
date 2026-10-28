package com.berkay.personalfinance.controller;

import com.berkay.personalfinance.dto.CategoryRequestDto;
import com.berkay.personalfinance.dto.CategoryResponseDto;
import com.berkay.personalfinance.entity.Category;
import com.berkay.personalfinance.enums.CategoryType;
import com.berkay.personalfinance.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CategoryResponseDto> createCategory(@Valid @RequestBody CategoryRequestDto categoryRequestDto, @RequestHeader(value = "X-User-Id", defaultValue = "1") Long userId) {
        CategoryResponseDto createdCategory = categoryService.createCategory(categoryRequestDto, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCategory);

    }

    @GetMapping
    public ResponseEntity<List<CategoryResponseDto>> getAllCategories(@RequestParam(required = false) CategoryType type, @RequestHeader(value = "X-User-Id", defaultValue = "1") Long userId) {
        List<CategoryResponseDto> categories = categoryService.getCategoriesByUser(userId, type);
        return ResponseEntity.status(HttpStatus.OK).body(categories);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponseDto> getCategoryById(@PathVariable Long id, @RequestHeader(value = "X-User-Id", defaultValue = "1") Long userId) {
        CategoryResponseDto categoryResponseDto = categoryService.getCategoryById(id, userId);
        return ResponseEntity.status(HttpStatus.OK).body(categoryResponseDto);

    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponseDto> updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryRequestDto categoryRequestDto, @RequestHeader(value = "X-User-Id", defaultValue = "1") Long userId) {
        CategoryResponseDto updatedCategory = categoryService.updateCategory(id, categoryRequestDto, userId);
        return ResponseEntity.status(HttpStatus.OK).body(updatedCategory);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Id", defaultValue = "1") Long userId
    ) {
        categoryService.deleteCategory(id, userId);
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}
