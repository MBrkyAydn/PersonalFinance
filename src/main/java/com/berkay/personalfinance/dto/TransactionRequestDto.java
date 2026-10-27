package com.berkay.personalfinance.dto;


import com.berkay.personalfinance.enums.TransactionType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionRequestDto {

    @NotNull(message = "İşlem tutarı boş olamaz")
    @Positive(message = "İşlem tutarı sıfırdan büyük pozitif bir değer olmalıdır")
    private BigDecimal amount;

    @Size(max = 255, message = "Açıklama en fazla 255 karakter olabilir")
    private String description;
    @NotNull(message = "İşlem tarihi belirtilmelidir")
    private LocalDate transactionDate;
    @NotNull(message = "İşlem türü (INCOME veya EXPENSE) belirtilmelidir")
    private TransactionType type;
    @NotNull(message = "Kategori ID belirtilmelidir")
    private Long categoryId;
}
