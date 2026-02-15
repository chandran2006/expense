package com.finmate.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReminderDTO {
    private Long id;
    private String title;
    private String message;
    private LocalDateTime reminderDate;
    private Boolean isCompleted;
    private LocalDateTime createdAt;
}
