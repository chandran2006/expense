package com.finmate.ai.controller;

import com.finmate.ai.dto.ApiResponse;
import com.finmate.ai.dto.ReminderDTO;
import com.finmate.ai.dto.ReminderRequest;
import com.finmate.ai.service.ReminderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reminders")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Reminders", description = "Reminder management APIs")
public class ReminderController {
    
    private final ReminderService reminderService;
    
    @PostMapping
    @Operation(summary = "Create reminder")
    public ResponseEntity<ApiResponse<ReminderDTO>> createReminder(@Valid @RequestBody ReminderRequest request) {
        ReminderDTO reminder = reminderService.createReminder(request);
        return ResponseEntity.ok(ApiResponse.success("Reminder created successfully", reminder));
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update reminder")
    public ResponseEntity<ApiResponse<ReminderDTO>> updateReminder(
            @PathVariable Long id,
            @Valid @RequestBody ReminderRequest request) {
        ReminderDTO reminder = reminderService.updateReminder(id, request);
        return ResponseEntity.ok(ApiResponse.success("Reminder updated successfully", reminder));
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete reminder")
    public ResponseEntity<ApiResponse<Void>> deleteReminder(@PathVariable Long id) {
        reminderService.deleteReminder(id);
        return ResponseEntity.ok(ApiResponse.success("Reminder deleted successfully", null));
    }
    
    @GetMapping
    @Operation(summary = "Get all reminders")
    public ResponseEntity<ApiResponse<List<ReminderDTO>>> getAllReminders() {
        List<ReminderDTO> reminders = reminderService.getAllReminders();
        return ResponseEntity.ok(ApiResponse.success("Reminders retrieved successfully", reminders));
    }
}
