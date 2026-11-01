package com.berkay.personalfinance.serviceimp;

import com.berkay.personalfinance.requestdto.LoginRequestDto;
import com.berkay.personalfinance.requestdto.RegisterRequestDto;
import com.berkay.personalfinance.responsedto.AuthResponseDto;
import com.berkay.personalfinance.responsedto.UserResponseDto;
import com.berkay.personalfinance.entity.User;
import com.berkay.personalfinance.exception.DuplicateResourceException;
import com.berkay.personalfinance.mapper.UserMapper;
import com.berkay.personalfinance.repository.UserRepository;
import com.berkay.personalfinance.security.CustomUserDetails;
import com.berkay.personalfinance.security.JwtService;
import com.berkay.personalfinance.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

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

    @Override
    public AuthResponseDto login(LoginRequestDto loginRequestDto) {
        // 1. Email ve şifreyi kontrol et (Yanlışsa Spring Security otomatik BadCredentialsException fırlatır)
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequestDto.getEmail(), loginRequestDto.getPassword()
                )
        );
        // 2. Doğrulanan kullanıcıyı al
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        // 3. Kullanıcıya özel JWT token üret
        String token = jwtService.generateToken(customUserDetails);
        // 4. Token'ı Bearer tipiyle kullanıcıya dön
        return AuthResponseDto.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .build();

    }
}
