package com.berkay.personalfinance.mapper;


import com.berkay.personalfinance.dto.TransactionRequestDto;
import com.berkay.personalfinance.dto.TransactionResponseDto;
import com.berkay.personalfinance.entity.Category;
import com.berkay.personalfinance.entity.Transaction;
import com.berkay.personalfinance.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TransactionMapper {

    private final CategoryMapper categoryMapper;

    public Transaction toEntity(TransactionRequestDto transactionRequestDto, User user, Category category) {
        if (transactionRequestDto == null) {
            return null;
        }

        return
                Transaction.builder()
                        .amount(transactionRequestDto.getAmount())
                        .type(transactionRequestDto.getType())
                        .category(category)
                        .user(user)
                        .build();

    }

    public TransactionResponseDto toResponseDto(Transaction transaction) {

        if (transaction == null) {
            return null;
        }

        return TransactionResponseDto.builder()
                .id(transaction.getId())
                .amount(transaction.getAmount())
                .description(transaction.getDescription())
                .transactionDate(transaction.getTransactionDate())
                .type(transaction.getType())
                .category(categoryMapper.toResponseDto(transaction.getCategory()))
                .createdAt(transaction.getCreatedAt())
                .updatedAt(transaction.getUpdatedAt())
                .build();
    }
}

