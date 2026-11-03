package com.berkay.personalfinance.serviceimp;

import com.berkay.personalfinance.entity.Transaction;
import com.berkay.personalfinance.enums.TransactionType;
import com.berkay.personalfinance.repository.TransactionRepository;
import com.berkay.personalfinance.responsedto.CategoryExpenseDto;
import com.berkay.personalfinance.responsedto.DashboardSummaryDto;
import com.berkay.personalfinance.service.DashboardService;
 import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {
private  final TransactionRepository transactionRepository;

@Override
    public DashboardSummaryDto getDashboardSummary(Long userId) {
    // 1. Toplam Geliri Çek (İşlem yoksa 0 döner)
    BigDecimal totalIncome=transactionRepository.sumAmountByUserIdAndType(userId, TransactionType.INCOME);

    // 2. Toplam Gideri Çek (İşlem yoksa 0 döner)
    BigDecimal totalExpense = transactionRepository.sumAmountByUserIdAndType(userId, TransactionType.EXPENSE);

    // 3. Net Bakiyeyi Hesapla: Gelir - Gider
    BigDecimal netBalance = totalIncome.subtract(totalExpense);


    // 4. Kategori bazlı harcama dağılımını çek (En çok harcanandan aza doğru)
    List<CategoryExpenseDto> categoryExpenses = transactionRepository
            .findCategoryExpensesByUserIdAndType(userId, TransactionType.EXPENSE);


    // 5. DashboardSummaryDto nesnesi oluştur ve dön
    return DashboardSummaryDto.builder()
            .totalIncome(totalIncome)
            .totalExpense(totalExpense)
            .netBalance(netBalance)
            .categoryExpenses(categoryExpenses)
            .build();

}

}
