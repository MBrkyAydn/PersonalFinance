package com.berkay.personalfinance.responsedto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminSystemStatsDto {
    private long totalUsers;
    private long totalCategories;
    private long totalTransactions;

}
