package com.berkay.personalfinance.service;

import com.berkay.personalfinance.dto.RegisterRequestDto;
import com.berkay.personalfinance.dto.UserResponseDto;

public interface AuthService {

UserResponseDto register(RegisterRequestDto registerRequestDto);

}
