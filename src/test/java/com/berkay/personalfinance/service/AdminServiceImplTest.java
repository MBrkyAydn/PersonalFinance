package com.berkay.personalfinance.service;

import com.berkay.personalfinance.entity.User;
import com.berkay.personalfinance.enums.Role;
import com.berkay.personalfinance.mapper.UserMapper;
import com.berkay.personalfinance.repository.CategoryRepository;
import com.berkay.personalfinance.repository.TransactionRepository;
import com.berkay.personalfinance.repository.UserRepository;
import com.berkay.personalfinance.responsedto.AdminSystemStatsDto;
import com.berkay.personalfinance.responsedto.UserResponseDto;
import com.berkay.personalfinance.serviceimp.AdminServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private AdminServiceImpl adminService;

    @Test
    @DisplayName("getAllUsers: Sistemde kayıtlı kullanıcılar varsa DTO listesi dönmeli")
    void getAllUsers_WhenUsersExist_ShouldReturnUserResponseDtoList() {
        // GIVEN - Unit test Mockito ile RAM üzerinde çalışır; AdminService şifreye hiç ihtiyaç duymaz
        User user1 = User.builder()
                .email("admin@example.com")
                .role(Role.ROLE_ADMIN)
                .build();
        user1.setId(1L);

        User user2 = User.builder()
                .email("ahmet@example.com")
                .role(Role.ROLE_USER)
                .build();
        user2.setId(2L);

        UserResponseDto dto1 = UserResponseDto.builder()
                .id(1L)
                .email("admin@example.com")
                .role(Role.ROLE_ADMIN)
                .createdDate(LocalDateTime.now())
                .build();

        UserResponseDto dto2 = UserResponseDto.builder()
                .id(2L)
                .email("ahmet@example.com")
                .role(Role.ROLE_USER)
                .createdDate(LocalDateTime.now())
                .build();

        when(userRepository.findAll()).thenReturn(List.of(user1, user2));
        when(userMapper.toResponseDto(user1)).thenReturn(dto1);
        when(userMapper.toResponseDto(user2)).thenReturn(dto2);

        // WHEN
        List<UserResponseDto> result = adminService.getAllUsers();

        // THEN
        assertThat(result).isNotNull().hasSize(2);
        assertThat(result.get(0).getEmail()).isEqualTo("admin@example.com");
        assertThat(result.get(0).getRole()).isEqualTo(Role.ROLE_ADMIN);
        assertThat(result.get(1).getEmail()).isEqualTo("ahmet@example.com");
        assertThat(result.get(1).getRole()).isEqualTo(Role.ROLE_USER);

        verify(userRepository).findAll();
        verify(userMapper).toResponseDto(user1);
        verify(userMapper).toResponseDto(user2);
    }

    @Test
    @DisplayName("getAllUsers: Sistemde hiç kullanıcı yoksa boş liste dönmeli")
    void getAllUsers_WhenNoUsersExist_ShouldReturnEmptyList() {
        // GIVEN
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        // WHEN
        List<UserResponseDto> result = adminService.getAllUsers();

        // THEN
        assertThat(result).isNotNull().isEmpty();

        verify(userRepository).findAll();
        verifyNoInteractions(userMapper);
    }

    @Test
    @DisplayName("getAdminSystemStats: Kullanıcı, kategori ve işlem sayılarını doğru toplayıp DTO olarak dönmeli")
    void getAdminSystemStats_ShouldReturnCorrectSystemCounts() {
        // GIVEN
        when(userRepository.count()).thenReturn(5L);
        when(categoryRepository.count()).thenReturn(12L);
        when(transactionRepository.count()).thenReturn(45L);

        // WHEN
        AdminSystemStatsDto stats = adminService.getAdminSystemStats();

        // THEN
        assertThat(stats).isNotNull();
        assertThat(stats.getTotalUsers()).isEqualTo(5L);
        assertThat(stats.getTotalCategories()).isEqualTo(12L);
        assertThat(stats.getTotalTransactions()).isEqualTo(45L);

        verify(userRepository).count();
        verify(categoryRepository).count();
        verify(transactionRepository).count();
    }
}
