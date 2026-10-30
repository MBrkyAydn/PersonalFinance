package com.berkay.personalfinance.mapper;

import com.berkay.personalfinance.dto.RegisterRequestDto;
import com.berkay.personalfinance.dto.UserResponseDto;
import com.berkay.personalfinance.entity.User;
import com.berkay.personalfinance.enums.Role;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    // RegisterRequestDto + BCrypt ile Hashlenmiş Şifre -> User Entity
    public User toEntiy(RegisterRequestDto registerRequestDto, String encodedPassword) {
        if (registerRequestDto == null) {
            return null;
        }
        return User.builder()
                .email(registerRequestDto.getEmail())
                .password(encodedPassword)
                .role(Role.ROLE_USER) //yeni kaydoloan varsayılan user
                .build();
    }
    // User Entity -> UserResponseDto
    public UserResponseDto toResponseDto(User user) {
        if (user == null) {
            return null;

        }
        return UserResponseDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .role(user.getRole())
                .createdDate(user.getCreatedAt())
                .build();
    }

}
