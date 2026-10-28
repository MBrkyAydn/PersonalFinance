package com.berkay.personalfinance.controller;

import com.berkay.personalfinance.dto.TransactionRequestDto;
import com.berkay.personalfinance.dto.TransactionResponseDto;
import com.berkay.personalfinance.enums.TransactionType;
import com.berkay.personalfinance.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/transaction")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<TransactionResponseDto> createTransaction(@Valid @RequestBody TransactionRequestDto transactionRequestDto, @RequestHeader(value = "X-User-Id", defaultValue = "1") Long userId) {
        TransactionResponseDto transactionResponseDto = transactionService.createTransaction(transactionRequestDto, userId);

        return ResponseEntity.status(HttpStatus.CREATED).body(transactionResponseDto);


    }

    @GetMapping
    ///Sayfalı ve Filtreli İşlem Listele (GET /api/v1/transactions?page=0&size=10&type=EXPENSE&startDate=2026-01-01)
    public ResponseEntity<Page<TransactionResponseDto>> getTransactions(@RequestParam(required = false) TransactionType type, @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate, @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate, @PageableDefault(size = 10, sort = "transactionDate", direction = Sort.Direction.DESC) Pageable pageable, @RequestHeader(value = "X-User-Id", defaultValue = "1") Long userId) {

        Page<TransactionResponseDto> transactions = transactionService.getTransactions(userId, type, startDate, endDate, pageable);
        return ResponseEntity.ok(transactions);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransactionResponseDto> updateTransaction(
            @PathVariable Long id,
            @Valid @RequestBody TransactionRequestDto requestDto,
            @RequestHeader(value = "X-User-Id", defaultValue = "1") Long userId
    ) {
        TransactionResponseDto updated = transactionService.updateTransaction(id, requestDto, userId);
        return ResponseEntity.ok(updated);
    }

    // 5. İşlem Sil (DELETE /api/v1/transactions/{id})
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Id", defaultValue = "1") Long userId
    ) {
        transactionService.deleteTransaction(id, userId);
        return ResponseEntity.noContent().build(); // 204 No Content
    }

}