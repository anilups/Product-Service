package com.example.product.dto.response;

import com.example.product.dto.request.ProductDimensionsDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private String id;
    private String sku;
    private String name;
    private String description;
    private String category;
    private BigDecimal price;
    private BigDecimal costPrice;
    private String brand;
    private Integer currentStock;
    private Integer minStockLevel;
    private Integer maxStockLevel;
    private Integer reorderPoint;
    private Integer reorderQuantity;
    private String unit;
    private String status;
    private List<String> tags;
    private List<String> imageUrls;
    private ProductDimensionsDto dimensions;
    private Double weight;
    private String warehouse;
    private String supplier;
    private Boolean isLowStock;
    private Boolean needsReorder;
    private Boolean isOutOfStock;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}
