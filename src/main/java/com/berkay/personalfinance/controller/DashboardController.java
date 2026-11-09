package com.berkay.personalfinance.controller;

import com.berkay.personalfinance.responsedto.DashboardSummaryDto;
import com.berkay.personalfinance.security.CustomUserDetails;
import com.berkay.personalfinance.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "Finansal özet ve istatistik işlemleri")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/summary")
    @Operation(summary = "Kullanıcının finansal özetini getirir (Toplam gelir, gider, net bakiye ve kategori dağılımı)")
    public ResponseEntity<DashboardSummaryDto> getSummary(@AuthenticationPrincipal CustomUserDetails userDetails) {
        DashboardSummaryDto summaryDto = dashboardService.getDashboardSummary(userDetails.getId());
        return ResponseEntity.ok(summaryDto);
    }
}
