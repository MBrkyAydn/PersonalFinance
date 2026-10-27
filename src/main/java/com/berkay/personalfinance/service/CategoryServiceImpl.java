package com.berkay.personalfinance.service;

import com.berkay.personalfinance.dto.CategoryRequestDto;
import com.berkay.personalfinance.dto.CategoryResponseDto;
import com.berkay.personalfinance.entity.Category;
import com.berkay.personalfinance.entity.User;
import com.berkay.personalfinance.enums.CategoryType;
import com.berkay.personalfinance.exception.DuplicateResourceException;
import com.berkay.personalfinance.exception.ResourceNotFoundException;
import com.berkay.personalfinance.mapper.CategoryMapper;
import com.berkay.personalfinance.repository.CategoryRepository;
import com.berkay.personalfinance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final CategoryMapper categoryMapper;

    @Override
    @Transactional // Veritabanına yazma işlemi yaptığı için readOnly = false
    public CategoryResponseDto createCategory(CategoryRequestDto requestDto, Long userId) {
        // 1. İş Kuralı: Kullanıcı veritabanında var mı?

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Kullanıcı bulunamadı, ID: " + userId));
        // 2. İş Kuralı: Bu kullanıcının bu isimde başka bir kategorisi var mı?
        if (categoryRepository.existsByNameIgnoreCaseAndUserId(requestDto.getName().trim(), userId)) {
            throw new DuplicateResourceException("'" + requestDto.getName() + "' adında bir kategori zaten mevcut.");

        }
        Category category = categoryMapper.toEntity(requestDto, user);
        Category savedCategory = categoryRepository.save(category);
        return categoryMapper.toResponseDto(savedCategory);

    }

    @Override
    public List<CategoryResponseDto> getCategoriesByUser(Long userId, CategoryType type) {
        List<Category> categories;
        if (type != null) {
            categories = categoryRepository.findByUserIdAndType(userId, type);

        } else {
            categories = categoryRepository.findByUserId(userId);
        }
        return categories.stream()
                .map(categoryMapper::toResponseDto)
                .toList();

    }

    @Override
    public CategoryResponseDto getCategoryById(Long id, Long userId) {
        Category category = categoryRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Kategori bulunamadı veya erişim yetkiniz yok, ID: " + id));
        return categoryMapper.toResponseDto(category);

    }

    @Override
    @Transactional
    public CategoryResponseDto updateCategory(Long id, CategoryRequestDto requestDto, Long userId) {
        Category category = categoryRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Kategori bulunamadı veya erişim yetkiniz yok, ID: " + id));
// İsim değişiyorsa, yeni ismin bu kullanıcıda zaten var olup olmadığını denetle
        String newName = requestDto.getName().trim();
        if (!category.getName().equalsIgnoreCase(newName) &&
                categoryRepository.existsByNameIgnoreCaseAndUserId(newName, userId)) {
            throw new DuplicateResourceException("'" + newName + "' adında bir kategori zaten mevcut.");
        }
        category.setName(newName);
        category.setType(requestDto.getCategoryType());
        Category updatedCategory = categoryRepository.save(category);
        return categoryMapper.toResponseDto(updatedCategory);

    }
    @Override
    @Transactional
    public void deleteCategory(Long id, Long userId) {
        Category category = categoryRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Kategori bulunamadı veya erişim yetkiniz yok, ID: " + id));
        categoryRepository.delete(category);
    }
}
