package com.berkay.personalfinance.responsedto;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class DashboardSummaryDto {
private BigDecimal totalIncome;
private BigDecimal totalExpense;
private BigDecimal netBalance;

private List<CategoryExpenseDto> categoryExpenses;


}
