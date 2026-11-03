package com.berkay.personalfinance.serviceimp;

import com.berkay.personalfinance.mapper.UserMapper;
import com.berkay.personalfinance.repository.CategoryRepository;
import com.berkay.personalfinance.repository.TransactionRepository;
import com.berkay.personalfinance.repository.UserRepository;
import com.berkay.personalfinance.responsedto.AdminSystemStatsDto;
import com.berkay.personalfinance.responsedto.UserResponseDto;
import com.berkay.personalfinance.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;
    private final UserMapper userMapper;

    @Override
    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toResponseDto)
                .toList();
    }
    @Override
    public AdminSystemStatsDto getAdminSystemStats() {
        return AdminSystemStatsDto.builder()
                .totalUsers(userRepository.count())
                .totalCategories(categoryRepository.count())
                .totalTransactions(transactionRepository.count())
                .build();
    }
}
