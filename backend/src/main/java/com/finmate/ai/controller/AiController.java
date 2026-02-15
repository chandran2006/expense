package com.finmate.ai.controller;

import com.finmate.ai.dto.ApiResponse;
import com.finmate.ai.dto.ChatRequest;
import com.finmate.ai.dto.ChatResponse;
import com.finmate.ai.service.AiInsightService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "AI Chatbot", description = "AI-powered financial insights")
public class AiController {
    
    private final AiInsightService aiInsightService;
    
    @PostMapping("/chat")
    @Operation(summary = "Chat with AI assistant", 
               description = "Ask questions about your finances, spending, budget, and get saving tips")
    public ResponseEntity<ApiResponse<ChatResponse>> chat(@Valid @RequestBody ChatRequest request) {
        ChatResponse response = aiInsightService.processMessage(request.getMessage());
        return ResponseEntity.ok(ApiResponse.success("Response generated successfully", response));
    }
}
