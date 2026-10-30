package com.berkay.personalfinance.service;

import com.berkay.personalfinance.dto.CategoryRequestDto;
import com.berkay.personalfinance.dto.CategoryResponseDto;
import com.berkay.personalfinance.entity.Category;
import com.berkay.personalfinance.entity.User;
import com.berkay.personalfinance.enums.CategoryType;
import com.berkay.personalfinance.enums.Role;
import com.berkay.personalfinance.exception.DuplicateResourceException;
import com.berkay.personalfinance.exception.ResourceNotFoundException;
import com.berkay.personalfinance.mapper.CategoryMapper;
import com.berkay.personalfinance.repository.CategoryRepository;
import com.berkay.personalfinance.repository.UserRepository;
import com.berkay.personalfinance.serviceimp.CategoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private User mockUser;
    private Category mockCategory;
    private CategoryRequestDto requestDto;
    private CategoryResponseDto responseDto;

    @BeforeEach
    void setUp() {
        // Her testten önce temiz sahte test verileri hazırlıyoruz
        mockUser = User.builder()
                .email("test@example.com")
                .password("hash123")
                .role(Role.ROLE_USER)
                .build();
        mockUser.setId(1L);

        mockCategory = Category.builder()
                .name("Market")
                .type(CategoryType.EXPENSE)
                .user(mockUser)
                .build();
        mockCategory.setId(10L);

        requestDto = CategoryRequestDto.builder()
                .name("Market")
                .categoryType(CategoryType.EXPENSE)
                .build();

        responseDto = CategoryResponseDto.builder()
                .id(10L)
                .name("Market")
                .categoryType(CategoryType.EXPENSE)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Başarılı kategori oluşturma senaryosu")
    void createCategory_WhenValidRequest_ShouldReturnResponseDto() {
        // Arrange (Hazırlık)
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(categoryRepository.existsByNameIgnoreCaseAndUserId("Market", 1L)).thenReturn(false);
        when(categoryMapper.toEntity(requestDto, mockUser)).thenReturn(mockCategory);
        when(categoryRepository.save(mockCategory)).thenReturn(mockCategory);
        when(categoryMapper.toResponseDto(mockCategory)).thenReturn(responseDto);

        // Act (Çalıştırma)
        CategoryResponseDto result = categoryService.createCategory(requestDto, 1L);

        // Assert (Doğrulama)
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(10L);
        assertThat(result.getName()).isEqualTo("Market");
        verify(categoryRepository, times(1)).save(mockCategory);
    }

    @Test
    @DisplayName("Kullanıcı bulunamadığında ResourceNotFoundException fırlatmalı")
    void createCategory_WhenUserNotFound_ShouldThrowResourceNotFoundException() {
        // Arrange
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () ->
                categoryService.createCategory(requestDto, 99L)
        );

        // Kullanıcı yoksa save() metodunun ASLA çağrılmadığını doğrula
        verify(categoryRepository, never()).save(any());
    }

    @Test
    @DisplayName("Aynı isimde kategori zaten varsa DuplicateResourceException fırlatmalı")
    void createCategory_WhenDuplicateName_ShouldThrowDuplicateResourceException() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(categoryRepository.existsByNameIgnoreCaseAndUserId("Market", 1L)).thenReturn(true);

        // Act & Assert
        assertThrows(DuplicateResourceException.class, () ->
                categoryService.createCategory(requestDto, 1L)
        );

        verify(categoryRepository, never()).save(any());
    }

    @Test
    @DisplayName("Başka kullanıcının kategorisi istendiğinde veya bulunamadığında hata fırlatmalı (Veri İzolasyonu)")
    void getCategoryById_WhenNotOwnedByUser_ShouldThrowResourceNotFoundException() {
        // Arrange
        when(categoryRepository.findByIdAndUserId(10L, 2L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () ->
                categoryService.getCategoryById(10L, 2L)
        );
    }
}