package com.berkay.personalfinance.service;

import com.berkay.personalfinance.enums.TransactionType;
import com.berkay.personalfinance.repository.TransactionRepository;
import com.berkay.personalfinance.responsedto.CategoryExpenseDto;
import com.berkay.personalfinance.responsedto.DashboardSummaryDto;
import com.berkay.personalfinance.serviceimp.DashboardServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DashboardServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private DashboardServiceImpl dashboardService;

    @Test
    @DisplayName("getDashboardSummary: Kullanıcının gelir ve giderleri varsa doğru özet ve net bakiye dönmeli")
    void getDashboardSummary_WhenUserHasTransactions_ShouldReturnCalculatedSummary() {
        // GIVEN
        Long userId = 1L;
        BigDecimal mockIncome = new BigDecimal("10000.00");
        BigDecimal mockExpense = new BigDecimal("3500.00");
        List<CategoryExpenseDto> mockCategoryExpenses = List.of(
                new CategoryExpenseDto("Kira", new BigDecimal("2500.00")),
                new CategoryExpenseDto("Market", new BigDecimal("1000.00"))
        );

        when(transactionRepository.sumAmountByUserIdAndType(userId, TransactionType.INCOME))
                .thenReturn(mockIncome);
        when(transactionRepository.sumAmountByUserIdAndType(userId, TransactionType.EXPENSE))
                .thenReturn(mockExpense);
        when(transactionRepository.findCategoryExpensesByUserIdAndType(userId, TransactionType.EXPENSE))
                .thenReturn(mockCategoryExpenses);

        // WHEN
        DashboardSummaryDto result = dashboardService.getDashboardSummary(userId);

        // THEN
        assertThat(result).isNotNull();
        assertThat(result.getTotalIncome()).isEqualTo(new BigDecimal("10000.00"));
        assertThat(result.getTotalExpense()).isEqualTo(new BigDecimal("3500.00"));
        // 10000 - 3500 = 6500 olmalı
        assertThat(result.getNetBalance()).isEqualTo(new BigDecimal("6500.00"));
        assertThat(result.getCategoryExpenses()).hasSize(2);
        assertThat(result.getCategoryExpenses().get(0).getCategoryName()).isEqualTo("Kira");

        verify(transactionRepository).sumAmountByUserIdAndType(userId, TransactionType.INCOME);
        verify(transactionRepository).sumAmountByUserIdAndType(userId, TransactionType.EXPENSE);
        verify(transactionRepository).findCategoryExpensesByUserIdAndType(userId, TransactionType.EXPENSE);
    }

    @Test
    @DisplayName("getDashboardSummary: Kullanıcının hiç işlemi yoksa gelir, gider ve bakiye 0 dönmeli")
    void getDashboardSummary_WhenUserHasNoTransactions_ShouldReturnZeroSummary() {
        // GIVEN
        Long userId = 2L;
        when(transactionRepository.sumAmountByUserIdAndType(userId, TransactionType.INCOME))
                .thenReturn(BigDecimal.ZERO);
        when(transactionRepository.sumAmountByUserIdAndType(userId, TransactionType.EXPENSE))
                .thenReturn(BigDecimal.ZERO);
        when(transactionRepository.findCategoryExpensesByUserIdAndType(userId, TransactionType.EXPENSE))
                .thenReturn(List.of());

        // WHEN
        DashboardSummaryDto result = dashboardService.getDashboardSummary(userId);

        // THEN
        assertThat(result).isNotNull();
        assertThat(result.getTotalIncome()).isEqualTo(BigDecimal.ZERO);
        assertThat(result.getTotalExpense()).isEqualTo(BigDecimal.ZERO);
        assertThat(result.getNetBalance()).isEqualTo(BigDecimal.ZERO);
        assertThat(result.getCategoryExpenses()).isEmpty();
    }
}