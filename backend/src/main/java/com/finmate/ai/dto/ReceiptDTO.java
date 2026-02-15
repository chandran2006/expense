package com.finmate.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptDTO {
    private Long id;
    private String imagePath;
    private Double extractedAmount;
    private LocalDate extractedDate;
    private String merchantName;
    private LocalDateTime createdAt;
}
