package com.example.product.mapper;

import com.example.product.document.*;
import com.example.product.dto.request.ProductDimensionsDto;
import com.example.product.dto.response.*;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public ProductResponse toResponse(ProductDocument document) {
        if (document == null) return null;

        return ProductResponse.builder()
                .id(document.getId())
                .sku(document.getSku())
                .name(document.getName())
                .description(document.getDescription())
                .category(document.getCategory())
                .price(document.getPrice())
                .costPrice(document.getCostPrice())
                .brand(document.getBrand())
                .currentStock(document.getCurrentStock())
                .minStockLevel(document.getMinStockLevel())
                .maxStockLevel(document.getMaxStockLevel())
                .reorderPoint(document.getReorderPoint())
                .reorderQuantity(document.getReorderQuantity())
                .unit(document.getUnit())
                .status(document.getStatus() != null ? document.getStatus().name() : null)
                .tags(document.getTags())
                .imageUrls(document.getImageUrls())
                .dimensions(toDimensionsDto(document.getDimensions()))
                .weight(document.getWeight())
                .warehouse(document.getWarehouse())
                .supplier(document.getSupplier())
                .isLowStock(document.isLowStock())
                .needsReorder(document.needsReorder())
                .isOutOfStock(document.isOutOfStock())
                .createdAt(document.getCreatedAt())
                .updatedAt(document.getUpdatedAt())
                .createdBy(document.getCreatedBy())
                .updatedBy(document.getUpdatedBy())
                .build();
    }

    public StockMovementResponse toMovementResponse(StockMovementDocument document) {
        if (document == null) return null;

        return StockMovementResponse.builder()
                .id(document.getId())
                .productId(document.getProductId())
                .productSku(document.getProductSku())
                .movementType(document.getMovementType() != null ? document.getMovementType().name() : null)
                .quantity(document.getQuantity())
                .previousStock(document.getPreviousStock())
                .newStock(document.getNewStock())
                .reason(document.getReason())
                .reference(document.getReference())
                .performedBy(document.getPerformedBy())
                .createdAt(document.getCreatedAt())
                .notes(document.getNotes())
                .build();
    }

    public StockAlertResponse toAlertResponse(StockAlertDocument document) {
        if (document == null) return null;

        return StockAlertResponse.builder()
                .id(document.getId())
                .productId(document.getProductId())
                .productSku(document.getProductSku())
                .productName(document.getProductName())
                .alertType(document.getAlertType() != null ? document.getAlertType().name() : null)
                .currentStock(document.getCurrentStock())
                .threshold(document.getThreshold())
                .status(document.getStatus() != null ? document.getStatus().name() : null)
                .triggeredAt(document.getTriggeredAt())
                .resolvedAt(document.getResolvedAt())
                .resolvedBy(document.getResolvedBy())
                .notes(document.getNotes())
                .build();
    }

    public ProductDimensions toDimensions(ProductDimensionsDto dto) {
        if (dto == null) return null;

        return ProductDimensions.builder()
                .length(dto.getLength())
                .width(dto.getWidth())
                .height(dto.getHeight())
                .unit(dto.getUnit())
                .build();
    }

    public ProductDimensionsDto toDimensionsDto(ProductDimensions dimensions) {
        if (dimensions == null) return null;

        return ProductDimensionsDto.builder()
                .length(dimensions.getLength())
                .width(dimensions.getWidth())
                .height(dimensions.getHeight())
                .unit(dimensions.getUnit())
                .build();
    }
}