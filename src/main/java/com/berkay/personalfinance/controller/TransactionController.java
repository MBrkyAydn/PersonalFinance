package com.berkay.personalfinance.controller;

import com.berkay.personalfinance.dto.TransactionRequestDto;
import com.berkay.personalfinance.dto.TransactionResponseDto;
import com.berkay.personalfinance.enums.TransactionType;
import com.berkay.personalfinance.security.CustomUserDetails;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/transaction")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<TransactionResponseDto> createTransaction(@Valid @RequestBody TransactionRequestDto transactionRequestDto,  @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        TransactionResponseDto transactionResponseDto = transactionService.createTransaction(transactionRequestDto, customUserDetails.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(transactionResponseDto);


    }

    @GetMapping
    ///Sayfalı ve Filtreli İşlem Listele (GET /api/v1/transactions?page=0&size=10&type=EXPENSE&startDate=2026-01-01)
    public ResponseEntity<Page<TransactionResponseDto>> getTransactions(@RequestParam(required = false) TransactionType type, @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate, @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate, @PageableDefault(size = 10, sort = "transactionDate", direction = Sort.Direction.DESC) Pageable pageable,  @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        Page<TransactionResponseDto> transactions = transactionService.getTransactions(customUserDetails.getId(), type, startDate, endDate, pageable);
        return ResponseEntity.ok(transactions);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransactionResponseDto> updateTransaction(
            @PathVariable Long id,
            @Valid @RequestBody TransactionRequestDto requestDto,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        TransactionResponseDto updated = transactionService.updateTransaction(id, requestDto, customUserDetails.getId());
        return ResponseEntity.ok(updated);
    }

    // 5. İşlem Sil (DELETE /api/v1/transactions/{id})
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        transactionService.deleteTransaction(id, customUserDetails.getId());
        return ResponseEntity.noContent().build(); // 204 No Content
    }

}