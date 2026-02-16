package com.finmate.ai.controller;

import com.finmate.ai.dto.ApiResponse;
import com.finmate.ai.service.AdvancedAnalyticsService;
import com.finmate.ai.service.ExportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/advanced")
@RequiredArgsConstructor
@Tag(name = "Advanced Analytics", description = "Advanced analytics and export features")
public class AdvancedAnalyticsController {
    
    private final AdvancedAnalyticsService analyticsService;
    private final ExportService exportService;
    
    @GetMapping("/spending-pattern/{userId}")
    @Operation(summary = "Get spending pattern analysis")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSpendingPattern(@PathVariable Long userId) {
        Map<String, Object> pattern = analyticsService.getSpendingPattern(userId);
        return ResponseEntity.ok(ApiResponse.success("Spending pattern retrieved", pattern));
    }
    
    @GetMapping("/forecast/{userId}")
    @Operation(summary = "Get expense forecast")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getForecast(@PathVariable Long userId) {
        Map<String, Object> forecast = analyticsService.getForecast(userId);
        return ResponseEntity.ok(ApiResponse.success("Forecast generated", forecast));
    }
    
    @GetMapping("/export/csv/{userId}")
    @Operation(summary = "Export transactions to CSV")
    public ResponseEntity<String> exportCSV(
            @PathVariable Long userId,
            @RequestParam String startDate,
            @RequestParam String endDate) {
        String csv = exportService.exportToCSV(userId, LocalDate.parse(startDate), LocalDate.parse(endDate));
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=transactions.csv")
            .contentType(MediaType.parseMediaType("text/csv"))
            .body(csv);
    }
    
    @GetMapping("/report/{userId}")
    @Operation(summary = "Generate monthly report")
    public ResponseEntity<ApiResponse<String>> generateReport(
            @PathVariable Long userId,
            @RequestParam String month) {
        String report = exportService.generateReport(userId, month);
        return ResponseEntity.ok(ApiResponse.success("Report generated", report));
    }
}
