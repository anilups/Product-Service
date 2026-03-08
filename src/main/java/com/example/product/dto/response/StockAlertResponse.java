package com.example.product.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockAlertResponse {
    private String id;
    private String productId;
    private String productSku;
    private String productName;
    private String alertType;
    private Integer currentStock;
    private Integer threshold;
    private String status;
    private LocalDateTime triggeredAt;
    private LocalDateTime resolvedAt;
    private String resolvedBy;
    private String notes;
}
