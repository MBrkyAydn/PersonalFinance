package com.berkay.personalfinance.controller;

import com.berkay.personalfinance.responsedto.AdminSystemStatsDto;
import com.berkay.personalfinance.responsedto.UserResponseDto;
import com.berkay.personalfinance.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/v1/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')") // sınıf seviyesine kilit
@Tag(name = "Admin", description = "Sistem yöneticisi işlemleri (Sadece ADMIN)")
public class AdminController {
private final AdminService adminService;

    @GetMapping("/users")
    @Operation(summary = "Sistemdeki tüm kayıtlı kullanıcıları listeler (Sadece ADMIN)")
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }
    @GetMapping("/stats")
    @Operation(summary = "Sistem genel istatistiklerini getirir (Sadece ADMIN)")
    public ResponseEntity<AdminSystemStatsDto> getAdminSystemStats() {
        return ResponseEntity.ok(adminService.getAdminSystemStats());
    }

}
