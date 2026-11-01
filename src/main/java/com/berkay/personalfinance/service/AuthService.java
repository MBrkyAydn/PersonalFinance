package com.berkay.personalfinance.service;

import com.berkay.personalfinance.requestdto.LoginRequestDto;
import com.berkay.personalfinance.requestdto.RegisterRequestDto;
import com.berkay.personalfinance.responsedto.AuthResponseDto;
import com.berkay.personalfinance.responsedto.UserResponseDto;

public interface AuthService {

    UserResponseDto register(RegisterRequestDto registerRequestDto);
    AuthResponseDto login(LoginRequestDto loginRequestDto);



}
