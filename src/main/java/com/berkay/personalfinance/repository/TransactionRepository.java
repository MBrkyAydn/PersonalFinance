package com.berkay.personalfinance.repository;

import com.berkay.personalfinance.entity.Transaction;
import com.berkay.personalfinance.entity.User;
import com.berkay.personalfinance.enums.TransactionType;
import com.berkay.personalfinance.responsedto.CategoryExpenseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
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

    /**
     * Kullanıcının belirli bir işlem türündeki (GELIR veya GIDER) toplam tutarını hesaplar.
     * Hiç işlem yoksa COALESCE sayesinde NULL yerine 0 döner.
     */
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.user.id = :userId AND t.type = :type")
    BigDecimal sumAmountByUserIdAndType(@Param("userId") Long userId, @Param("type") TransactionType type);
    /**
     * Kullanıcının harcamalarını kategori bazında gruplayıp en çok harcanandan en aza doğru sıralar.
     * JPQL Constructor Expression (new ...) ile doğrudan DTO nesnesine dönüştürür.
     */
    @Query("SELECT new com.berkay.personalfinance.responsedto.CategoryExpenseDto(t.category.name, SUM(t.amount)) " +
            "FROM Transaction t " +
            "WHERE t.user.id = :userId AND t.type = :type " +
            "GROUP BY t.category.name " +
            "ORDER BY SUM(t.amount) DESC")
    List<CategoryExpenseDto> findCategoryExpensesByUserIdAndType(@Param("userId") Long userId, @Param("type") TransactionType type);

    Long user(User user);
}
