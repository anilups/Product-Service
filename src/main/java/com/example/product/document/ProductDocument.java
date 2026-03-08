package com.example.product.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.couchbase.core.mapping.Document;
import org.springframework.data.couchbase.core.mapping.Field;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document
public class ProductDocument {

    @Id
    private String id;

    @Field
    private String sku;

    @Field
    private String name;

    @Field
    private String description;

    @Field
    private String category;

    @Field
    private BigDecimal price;

    @Field
    private BigDecimal costPrice;

    @Field
    private String brand;

    @Field
    private Integer currentStock;

    @Field
    private Integer minStockLevel;

    @Field
    private Integer maxStockLevel;

    @Field
    private Integer reorderPoint;

    @Field
    private Integer reorderQuantity;

    @Field
    private String unit; // piece, kg, liter, etc.

    @Field
    private ProductStatus status;

    @Field
    private List<String> tags;

    @Field
    private List<String> imageUrls;

    @Field
    private ProductDimensions dimensions;

    @Field
    private Double weight;

    @Field
    private String warehouse;

    @Field
    private String supplier;

    @Field
    private LocalDateTime createdAt;

    @Field
    private LocalDateTime updatedAt;

    @Field
    private String createdBy;

    @Field
    private String updatedBy;

    public boolean isLowStock() {
        return currentStock != null && minStockLevel != null && currentStock <= minStockLevel;
    }

    public boolean needsReorder() {
        return currentStock != null && reorderPoint != null && currentStock <= reorderPoint;
    }

    public boolean isOutOfStock() {
        return currentStock != null && currentStock <= 0;
    }
}