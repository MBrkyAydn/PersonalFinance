package com.berkay.personalfinance.serviceimp;

import com.berkay.personalfinance.dto.RegisterRequestDto;
import com.berkay.personalfinance.dto.UserResponseDto;
import com.berkay.personalfinance.entity.User;
import com.berkay.personalfinance.exception.DuplicateResourceException;
import com.berkay.personalfinance.mapper.UserMapper;
import com.berkay.personalfinance.repository.UserRepository;
import com.berkay.personalfinance.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserResponseDto register(RegisterRequestDto registerRequestDto) {
        String email = registerRequestDto.getEmail().toLowerCase();

        if (userRepository.existsByEmail(email)) {
            throw new DuplicateResourceException("Bu e-posta adresi (" + email + ") zaten kullanımda");


        }
        String encodedPassword = passwordEncoder.encode(registerRequestDto.getPassword());

        User user = userMapper.toEntiy(registerRequestDto, encodedPassword);
        User savedUser = userRepository.save(user);

        return userMapper.toResponseDto(savedUser);


    }


}
