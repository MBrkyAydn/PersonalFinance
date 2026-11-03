package com.berkay.personalfinance.responsedto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryExpenseDto {
private String categoryName;
private BigDecimal totalAmount;



}
