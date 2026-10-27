package com.berkay.personalfinance.dto;

import com.berkay.personalfinance.enums.TransactionType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionResponseDto {


    private Long id;
    private BigDecimal amount;
    private String description;
    private LocalDate transactionDate;
    private TransactionType type;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private CategoryResponseDto category; // Kategori detaylarını iç içe temiz bir obje olarak döner

}
