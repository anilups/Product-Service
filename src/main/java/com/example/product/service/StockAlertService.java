package com.example.product.service;

import com.example.product.document.*;
import com.example.product.dto.response.StockAlertResponse;
import com.example.product.mapper.ProductMapper;
import com.example.product.repository.StockAlertRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockAlertService {

    private final StockAlertRepository stockAlertRepository;
    private final ProductMapper productMapper;

    @Transactional
    public void checkAndCreateAlerts(ProductDocument product) {
        // Check for out of stock
        if (product.isOutOfStock()) {
            createOrUpdateAlert(product, AlertType.OUT_OF_STOCK, 0);
        } else {
            resolveAlert(product.getId(), AlertType.OUT_OF_STOCK);
        }

        // Check for low stock
        if (product.isLowStock() && !product.isOutOfStock()) {
            createOrUpdateAlert(product, AlertType.LOW_STOCK, product.getMinStockLevel());
        } else {
            resolveAlert(product.getId(), AlertType.LOW_STOCK);
        }

        // Check for reorder point
        if (product.needsReorder() && !product.isOutOfStock()) {
            createOrUpdateAlert(product, AlertType.REORDER_POINT, product.getReorderPoint());
        } else {
            resolveAlert(product.getId(), AlertType.REORDER_POINT);
        }
    }

    private void createOrUpdateAlert(ProductDocument product, AlertType alertType, Integer threshold) {
        Optional<StockAlertDocument> existingAlert = stockAlertRepository
                .findByProductIdAndStatusAndAlertType(product.getId(), AlertStatus.ACTIVE, alertType);

        if (existingAlert.isEmpty()) {
            StockAlertDocument alert = StockAlertDocument.builder()
                    .id(UUID.randomUUID().toString())
                    .productId(product.getId())
                    .productSku(product.getSku())
                    .productName(product.getName())
                    .alertType(alertType)
                    .currentStock(product.getCurrentStock())
                    .threshold(threshold)
                    .status(AlertStatus.ACTIVE)
                    .triggeredAt(LocalDateTime.now())
                    .build();

            stockAlertRepository.save(alert);
            log.info("Created {} alert for product: {}", alertType, product.getSku());
        }
    }

    private void resolveAlert(String productId, AlertType alertType) {
        Optional<StockAlertDocument> existingAlert = stockAlertRepository
                .findByProductIdAndStatusAndAlertType(productId, AlertStatus.ACTIVE, alertType);

        existingAlert.ifPresent(alert -> {
            alert.setStatus(AlertStatus.RESOLVED);
            alert.setResolvedAt(LocalDateTime.now());
            alert.setResolvedBy("SYSTEM");
            stockAlertRepository.save(alert);
            log.info("Resolved {} alert for product ID: {}", alertType, productId);
        });
    }

    public List<StockAlertResponse> getActiveAlerts() {
        return stockAlertRepository.findByStatusOrderByTriggeredAtDesc(AlertStatus.ACTIVE)
                .stream()
                .map(productMapper::toAlertResponse)
                .collect(Collectors.toList());
    }

    public List<StockAlertResponse> getAlertsByType(AlertType alertType) {
        return stockAlertRepository.findByAlertType(alertType)
                .stream()
                .map(productMapper::toAlertResponse)
                .collect(Collectors.toList());
    }

    public List<StockAlertResponse> getAlertsForProduct(String productId) {
        return stockAlertRepository.findByProductId(productId)
                .stream()
                .map(productMapper::toAlertResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public StockAlertResponse acknowledgeAlert(String alertId) {
        StockAlertDocument alert = stockAlertRepository.findById(alertId)
                .orElseThrow(() -> new RuntimeException("Alert not found"));

        alert.setStatus(AlertStatus.ACKNOWLEDGED);
        StockAlertDocument updated = stockAlertRepository.save(alert);

        return productMapper.toAlertResponse(updated);
    }

    @Transactional
    public StockAlertResponse dismissAlert(String alertId, String notes) {
        StockAlertDocument alert = stockAlertRepository.findById(alertId)
                .orElseThrow(() -> new RuntimeException("Alert not found"));

        alert.setStatus(AlertStatus.DISMISSED);
        alert.setResolvedAt(LocalDateTime.now());
        alert.setResolvedBy(getCurrentUsername());
        alert.setNotes(notes);

        StockAlertDocument updated = stockAlertRepository.save(alert);

        return productMapper.toAlertResponse(updated);
    }

    private String getCurrentUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}