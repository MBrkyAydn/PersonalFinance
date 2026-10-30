package com.berkay.personalfinance.service;

import com.berkay.personalfinance.dto.CategoryResponseDto;
import com.berkay.personalfinance.dto.TransactionRequestDto;
import com.berkay.personalfinance.dto.TransactionResponseDto;
import com.berkay.personalfinance.entity.Category;
import com.berkay.personalfinance.entity.Transaction;
import com.berkay.personalfinance.entity.User;
import com.berkay.personalfinance.enums.CategoryType;
import com.berkay.personalfinance.enums.Role;
import com.berkay.personalfinance.enums.TransactionType;
import com.berkay.personalfinance.exception.BadRequestException;
import com.berkay.personalfinance.exception.ResourceNotFoundException;
import com.berkay.personalfinance.mapper.TransactionMapper;
import com.berkay.personalfinance.repository.CategoryRepository;
import com.berkay.personalfinance.repository.TransactionRepository;
import com.berkay.personalfinance.repository.UserRepository;
import com.berkay.personalfinance.serviceimp.TransactionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TransactionMapper transactionMapper;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private User mockUser;
    private Category mockCategory;
    private Transaction mockTransaction;
    private TransactionRequestDto requestDto;
    private TransactionResponseDto responseDto;

    @BeforeEach
    void setUp() {
        mockUser = User.builder()
                .email("ahmet@example.com")
                .password("pass123")
                .role(Role.ROLE_USER)
                .build();
        mockUser.setId(1L);

        mockCategory = Category.builder()
                .name("Market")
                .type(CategoryType.EXPENSE)
                .user(mockUser)
                .build();
        mockCategory.setId(10L);

        mockTransaction = Transaction.builder()
                .amount(new BigDecimal("150.50"))
                .description("Haftalık alışveriş")
                .transactionDate(LocalDate.now())
                .type(TransactionType.EXPENSE)
                .user(mockUser)
                .category(mockCategory)
                .build();
        mockTransaction.setId(100L);

        requestDto = TransactionRequestDto.builder()
                .amount(new BigDecimal("150.50"))
                .description("Haftalık alışveriş")
                .transactionDate(LocalDate.now())
                .type(TransactionType.EXPENSE)
                .categoryId(10L)
                .build();

        CategoryResponseDto categoryResponseDto = CategoryResponseDto.builder()
                .id(10L)
                .name("Market")
                .categoryType(CategoryType.EXPENSE)
                .build();

        responseDto = TransactionResponseDto.builder()
                .id(100L)
                .amount(new BigDecimal("150.50"))
                .description("Haftalık alışveriş")
                .transactionDate(LocalDate.now())
                .type(TransactionType.EXPENSE)
                .category(categoryResponseDto)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Başarılı işlem oluşturma senaryosu")
    void createTransaction_WhenValidRequest_ShouldReturnResponseDto() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(categoryRepository.findByIdAndUserId(10L, 1L)).thenReturn(Optional.of(mockCategory));
        when(transactionMapper.toEntity(requestDto, mockUser, mockCategory)).thenReturn(mockTransaction);
        when(transactionRepository.save(mockTransaction)).thenReturn(mockTransaction);
        when(transactionMapper.toResponseDto(mockTransaction)).thenReturn(responseDto);

        // Act
        TransactionResponseDto result = transactionService.createTransaction(requestDto, 1L);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(100L);
        assertThat(result.getAmount()).isEqualTo(new BigDecimal("150.50"));
        verify(transactionRepository, times(1)).save(mockTransaction);
    }

    @Test
    @DisplayName("Kategori kullanıcıya ait değilse ResourceNotFoundException fırlatmalı (Veri İzolasyonu)")
    void createTransaction_WhenCategoryNotOwnedByUser_ShouldThrowResourceNotFoundException() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(categoryRepository.findByIdAndUserId(10L, 1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () ->
                transactionService.createTransaction(requestDto, 1L)
        );

        verify(transactionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Kategori türü ile İşlem türü uyuşmazsa BadRequestException fırlatmalı")
    void createTransaction_WhenCategoryAndTransactionTypeMismatch_ShouldThrowBadRequestException() {
        // Arrange: Kategori GİDER (EXPENSE) iken işlem GELİR (INCOME) olarak gönderiliyor
        requestDto.setType(TransactionType.INCOME);

        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(categoryRepository.findByIdAndUserId(10L, 1L)).thenReturn(Optional.of(mockCategory));

        // Act & Assert
        assertThrows(BadRequestException.class, () ->
                transactionService.createTransaction(requestDto, 1L)
        );

        verify(transactionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Başka kullanıcının işlemine erişmeye çalışıldığında hata fırlatmalı")
    void getTransactionById_WhenNotOwnedByUser_ShouldThrowResourceNotFoundException() {
        // Arrange
        when(transactionRepository.findByIdAndUserId(100L, 2L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () ->
                transactionService.getTransactionById(100L, 2L)
        );
    }

    @Test
    @DisplayName("Başarılı işlem silme")
    void deleteTransaction_WhenSuccessful_ShouldCallDelete() {
        // Arrange
        when(transactionRepository.findByIdAndUserId(100L, 1L)).thenReturn(Optional.of(mockTransaction));
        doNothing().when(transactionRepository).delete(mockTransaction);

        // Act
        transactionService.deleteTransaction(100L, 1L);

        // Assert
        verify(transactionRepository, times(1)).delete(mockTransaction);
    }

    @Test
    @DisplayName("Sayfalı işlem listeleme başarılı olmalı")
    void getTransactions_WhenNoFilter_ShouldReturnPagedTransactions() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Transaction> transactionPage = new PageImpl<>(List.of(mockTransaction));

        when(transactionRepository.findByUserId(1L, pageable)).thenReturn(transactionPage);
        when(transactionMapper.toResponseDto(mockTransaction)).thenReturn(responseDto);

        // Act
        Page<TransactionResponseDto> result = transactionService.getTransactions(1L, null, null, null, pageable);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getId()).isEqualTo(100L);
        verify(transactionRepository, times(1)).findByUserId(1L, pageable);
    }
}
