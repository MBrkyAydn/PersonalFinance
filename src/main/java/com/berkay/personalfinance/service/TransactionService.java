package com.berkay.personalfinance.service;

import com.berkay.personalfinance.dto.TransactionRequestDto;
import com.berkay.personalfinance.dto.TransactionResponseDto;
import com.berkay.personalfinance.enums.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface TransactionService {
    TransactionResponseDto createTransaction(TransactionRequestDto requestDto, Long userId);

    Page<TransactionResponseDto> getTransactions(
            Long userId,
            TransactionType type,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable
    );

    TransactionResponseDto getTransactionById(Long id, Long userId);

    TransactionResponseDto updateTransaction(Long id, TransactionRequestDto requestDto, Long userId);

    void deleteTransaction(Long id, Long userId);

}
