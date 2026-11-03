package com.berkay.personalfinance.service;

import com.berkay.personalfinance.responsedto.AdminSystemStatsDto;
import com.berkay.personalfinance.responsedto.UserResponseDto;

import java.util.List;

public interface AdminService {

List<UserResponseDto> getAllUsers();
AdminSystemStatsDto getAdminSystemStats();

}
