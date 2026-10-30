package com.berkay.personalfinance.serviceimp;

import com.berkay.personalfinance.mapper.TransactionMapper;
import com.berkay.personalfinance.repository.CategoryRepository;
import com.berkay.personalfinance.repository.TransactionRepository;
import com.berkay.personalfinance.repository.UserRepository;
import com.berkay.personalfinance.dto.TransactionRequestDto;
import com.berkay.personalfinance.dto.TransactionResponseDto;
import com.berkay.personalfinance.entity.Category;
import com.berkay.personalfinance.entity.Transaction;
import com.berkay.personalfinance.entity.User;
import com.berkay.personalfinance.enums.CategoryType;
import com.berkay.personalfinance.enums.TransactionType;
import com.berkay.personalfinance.exception.BadRequestException;
import com.berkay.personalfinance.exception.ResourceNotFoundException;

import com.berkay.personalfinance.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TransactionServiceImpl implements TransactionService {
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;

    @Override
    @Transactional
    public TransactionResponseDto createTransaction(TransactionRequestDto requestDto, Long userId) {
        // 1. İş Kuralı: Kullanıcı var mı?
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Kullanıcı bulunamadı, ID: " + userId));
        // 2. İş Kuralı (Veri İzolasyonu): Seçilen kategori GERÇEKTEN bu kullanıcıya mı ait?
        Category category = categoryRepository.findByIdAndUserId(requestDto.getCategoryId(), userId)
                .orElseThrow(() -> new ResourceNotFoundException("Kategori bulunamadı veya bu kullanıcıya ait değil, ID: " + requestDto.getCategoryId()));
        // 3. İş Kuralı (Finansal Tutarlılık): Kategori türü ile işlem türü uyuşuyor mu?
        validateCategoryAndTransactionType(category.getType(), requestDto.getType());
        Transaction transaction = transactionMapper.toEntity(requestDto, user, category);
        Transaction savedTransaction = transactionRepository.save(transaction);
        return transactionMapper.toResponseDto(savedTransaction);
    }

    @Override
    public Page<TransactionResponseDto> getTransactions(
            Long userId,
            TransactionType type,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable
    ) {
        Page<Transaction> transactions;
        if (startDate != null && endDate != null) {
            // Tarih aralığına göre filtrele
            transactions = transactionRepository.findByUserIdAndTransactionDateBetween(userId, startDate, endDate, pageable);
        } else if (type != null) {
            // Sadece türe göre filtrele (Sadece gelirler veya sadece giderler)
            transactions = transactionRepository.findByUserIdAndType(userId, type, pageable);
        } else {
            // Filtresiz tüm işlemler (sayfalı)
            transactions = transactionRepository.findByUserId(userId, pageable);
        }
        return transactions.map(transactionMapper::toResponseDto);
    }

    @Override
    public TransactionResponseDto getTransactionById(Long id, Long userId) {
        Transaction transaction = transactionRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("İşlem bulunamadı veya erişim yetkiniz yok, ID: " + id));
        return transactionMapper.toResponseDto(transaction);
    }

    @Override
    @Transactional
    public TransactionResponseDto updateTransaction(Long id, TransactionRequestDto requestDto, Long userId) {
        Transaction transaction = transactionRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("İşlem bulunamadı veya erişim yetkiniz yok, ID: " + id));
        // Kategori değiştiyse, yeni kategorinin bu kullanıcıya ait olduğunu doğrula
        Category category = categoryRepository.findByIdAndUserId(requestDto.getCategoryId(), userId)
                .orElseThrow(() -> new ResourceNotFoundException("Kategori bulunamadı veya bu kullanıcıya ait değil, ID: " + requestDto.getCategoryId()));
        validateCategoryAndTransactionType(category.getType(), requestDto.getType());
        transaction.setAmount(requestDto.getAmount());
        transaction.setDescription(requestDto.getDescription() != null ? requestDto.getDescription().trim() : null);
        transaction.setTransactionDate(requestDto.getTransactionDate());
        transaction.setType(requestDto.getType());
        transaction.setCategory(category);
        Transaction updatedTransaction = transactionRepository.save(transaction);
        return transactionMapper.toResponseDto(updatedTransaction);
    }

    @Override
    @Transactional
    public void deleteTransaction(Long id, Long userId) {
        Transaction transaction = transactionRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("İşlem bulunamadı veya erişim yetkiniz yok, ID: " + id));
        transactionRepository.delete(transaction);
    }

    // Yardımcı Kontrol Metodu
    private void validateCategoryAndTransactionType(CategoryType categoryType, TransactionType transactionType) {
        if (!categoryType.name().equals(transactionType.name())) {
            throw new BadRequestException("İşlem türü (" + transactionType + ") ile Kategori türü (" + categoryType + ") uyuşmuyor!");
        }
    }
}
