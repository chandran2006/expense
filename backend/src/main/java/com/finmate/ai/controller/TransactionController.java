package com.finmate.ai.controller;

import com.finmate.ai.dto.*;
import com.finmate.ai.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Transactions", description = "Transaction management APIs")
public class TransactionController {
    
    private final TransactionService transactionService;
    
    @PostMapping
    @Operation(summary = "Add a new transaction")
    public ResponseEntity<ApiResponse<TransactionDTO>> addTransaction(@Valid @RequestBody TransactionRequest request) {
        TransactionDTO transaction = transactionService.addTransaction(request);
        return ResponseEntity.ok(ApiResponse.success("Transaction added successfully", transaction));
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update a transaction")
    public ResponseEntity<ApiResponse<TransactionDTO>> updateTransaction(
            @PathVariable Long id,
            @Valid @RequestBody TransactionRequest request) {
        TransactionDTO transaction = transactionService.updateTransaction(id, request);
        return ResponseEntity.ok(ApiResponse.success("Transaction updated successfully", transaction));
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a transaction")
    public ResponseEntity<ApiResponse<Void>> deleteTransaction(@PathVariable Long id) {
        transactionService.deleteTransaction(id);
        return ResponseEntity.ok(ApiResponse.success("Transaction deleted successfully", null));
    }
    
    @GetMapping
    @Operation(summary = "Get all transactions")
    public ResponseEntity<ApiResponse<List<TransactionDTO>>> getAllTransactions() {
        List<TransactionDTO> transactions = transactionService.getAllTransactions();
        return ResponseEntity.ok(ApiResponse.success("Transactions retrieved successfully", transactions));
    }
    
    @GetMapping("/summary/{month}")
    @Operation(summary = "Get monthly summary", description = "Month format: YYYY-MM (e.g., 2024-01)")
    public ResponseEntity<ApiResponse<MonthlySummaryDTO>> getMonthlySummary(@PathVariable String month) {
        MonthlySummaryDTO summary = transactionService.getMonthlySummary(month);
        return ResponseEntity.ok(ApiResponse.success("Monthly summary retrieved successfully", summary));
    }
}
