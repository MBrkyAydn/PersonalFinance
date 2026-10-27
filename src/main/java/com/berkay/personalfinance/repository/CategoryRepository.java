package com.berkay.personalfinance.repository;

import com.berkay.personalfinance.entity.Category;
import com.berkay.personalfinance.enums.CategoryType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findByUserId(Long userId);

    List<Category> findByUserIdAndType(Long userId, CategoryType type);

    //Bir kategori güncellenirken/silinirken o kategori GERÇEKTEN o kullanıcıya mı ait?
    Optional<Category> findByIdAndUserId(Long id, Long userId);

    boolean existsByNameIgnoreCaseAndUserId(String name, Long userId);
}

