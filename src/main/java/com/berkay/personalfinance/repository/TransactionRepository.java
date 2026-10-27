package com.berkay.personalfinance.repository;

import com.berkay.personalfinance.entity.Transaction;
import com.berkay.personalfinance.enums.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Optional<Transaction> findByIdAndUserId(Long id, Long userId);


    Page<Transaction> findByUserId(Long userId, Pageable pageable);
    Page<Transaction> findByUserIdAndType(Long userId, TransactionType type, Pageable pageable);
    Page<Transaction> findByUserIdAndTransactionDateBetween(
            Long userId,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable
    );


}
