package com.example.product.service;

import com.example.product.document.MovementType;
import com.example.product.document.ProductDocument;
import com.example.product.document.ProductStatus;
import com.example.product.document.StockMovementDocument;
import com.example.product.dto.request.CreateProductRequest;
import com.example.product.dto.request.StockAdjustmentRequest;
import com.example.product.dto.request.UpdateProductRequest;
import com.example.product.dto.response.ProductResponse;
import com.example.product.dto.response.ProductStatsResponse;
import com.example.product.dto.response.StockMovementResponse;
import com.example.product.exception.CustomException;
import com.example.product.mapper.ProductMapper;
import com.example.product.repository.ProductRepository;
import com.example.product.repository.StockMovementRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final StockMovementRepository stockMovementRepository;
    private final ProductMapper productMapper;
    private final StockAlertService stockAlertService;

    @Transactional
    public ProductResponse createProduct(CreateProductRequest request) {
        log.info("Creating product with SKU: {}", request.getSku());

        if (productRepository.existsBySku(request.getSku())) {
            throw CustomException.conflict("Product with SKU " + request.getSku() + " already exists");
        }

        String currentUser = getCurrentUsername();

        ProductDocument product = ProductDocument.builder()
                .id(UUID.randomUUID().toString())
                .sku(request.getSku())
                .name(request.getName())
                .description(request.getDescription())
                .category(request.getCategory())
                .price(request.getPrice())
                .costPrice(request.getCostPrice())
                .brand(request.getBrand())
                .currentStock(request.getInitialStock())
                .minStockLevel(request.getMinStockLevel())
                .maxStockLevel(request.getMaxStockLevel())
                .reorderPoint(request.getReorderPoint())
                .reorderQuantity(request.getReorderQuantity())
                .unit(request.getUnit())
                .status(ProductStatus.ACTIVE)
                .tags(request.getTags())
                .imageUrls(request.getImageUrls())
                .dimensions(productMapper.toDimensions(request.getDimensions()))
                .weight(request.getWeight())
                .warehouse(request.getWarehouse())
                .supplier(request.getSupplier())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .createdBy(currentUser)
                .updatedBy(currentUser)
                .build();

        ProductDocument savedProduct = productRepository.save(product);

        // Record initial stock movement
        if (request.getInitialStock() > 0) {
            recordStockMovement(savedProduct, MovementType.PURCHASE,
                    request.getInitialStock(), 0, "Initial stock",
                    "INITIAL", currentUser);
        }

        // Check for low stock alerts
        stockAlertService.checkAndCreateAlerts(savedProduct);

        log.info("Product created successfully with ID: {}", savedProduct.getId());
        return productMapper.toResponse(savedProduct);
    }

    public ProductResponse getProductById(String id) {
        ProductDocument product = findProductById(id);
        return productMapper.toResponse(product);
    }

    public ProductResponse getProductBySku(String sku) {
        ProductDocument product = productRepository.findBySku(sku)
                .orElseThrow(() -> CustomException.notFound("Product not found with SKU: " + sku));
        return productMapper.toResponse(product);
    }

    public Page<ProductResponse> getAllProducts(Pageable pageable) {
        Page<ProductDocument> products = productRepository.findAll(pageable);
        List<ProductResponse> responses = products.getContent().stream()
                .map(productMapper::toResponse)
                .collect(Collectors.toList());
        return new PageImpl<>(responses, pageable, products.getTotalElements());
    }

    public List<ProductResponse> searchProducts(String keyword) {
        List<ProductDocument> products = productRepository.searchProducts(keyword);
        return products.stream()
                .map(productMapper::toResponse)
                .collect(Collectors.toList());
    }

    public Page<ProductResponse> getProductsByCategory(String category, Pageable pageable) {
        Page<ProductDocument> products = productRepository.findByCategory(category, pageable);
        return products.map(productMapper::toResponse);
    }

    @Transactional
    public ProductResponse updateProduct(String id, UpdateProductRequest request) {
        log.info("Updating product with ID: {}", id);

        ProductDocument product = findProductById(id);
        String currentUser = getCurrentUsername();

        // Update fields if provided
        if (request.getName() != null) product.setName(request.getName());
        if (request.getDescription() != null) product.setDescription(request.getDescription());
        if (request.getCategory() != null) product.setCategory(request.getCategory());
        if (request.getPrice() != null) product.setPrice(request.getPrice());
        if (request.getCostPrice() != null) product.setCostPrice(request.getCostPrice());
        if (request.getBrand() != null) product.setBrand(request.getBrand());
        if (request.getMinStockLevel() != null) product.setMinStockLevel(request.getMinStockLevel());
        if (request.getMaxStockLevel() != null) product.setMaxStockLevel(request.getMaxStockLevel());
        if (request.getReorderPoint() != null) product.setReorderPoint(request.getReorderPoint());
        if (request.getReorderQuantity() != null) product.setReorderQuantity(request.getReorderQuantity());
        if (request.getUnit() != null) product.setUnit(request.getUnit());
        if (request.getStatus() != null) {
            product.setStatus(ProductStatus.valueOf(request.getStatus()));
        }
        if (request.getTags() != null) product.setTags(request.getTags());
        if (request.getImageUrls() != null) product.setImageUrls(request.getImageUrls());
        if (request.getDimensions() != null) {
            product.setDimensions(productMapper.toDimensions(request.getDimensions()));
        }
        if (request.getWeight() != null) product.setWeight(request.getWeight());
        if (request.getWarehouse() != null) product.setWarehouse(request.getWarehouse());
        if (request.getSupplier() != null) product.setSupplier(request.getSupplier());

        product.setUpdatedAt(LocalDateTime.now());
        product.setUpdatedBy(currentUser);

        ProductDocument updatedProduct = productRepository.save(product);

        // Check for stock alerts after update
        stockAlertService.checkAndCreateAlerts(updatedProduct);

        log.info("Product updated successfully: {}", id);
        return productMapper.toResponse(updatedProduct);
    }

    @Transactional
    public void deleteProduct(String id) {
        log.info("Deleting product with ID: {}", id);

        ProductDocument product = findProductById(id);

        // Soft delete by setting status to DISCONTINUED
        product.setStatus(ProductStatus.DISCONTINUED);
        product.setUpdatedAt(LocalDateTime.now());
        product.setUpdatedBy(getCurrentUsername());

        productRepository.save(product);

        log.info("Product deleted successfully: {}", id);
    }

    @Transactional
    public ProductResponse adjustStock(String id, StockAdjustmentRequest request) {
        log.info("Adjusting stock for product ID: {}", id);

        ProductDocument product = findProductById(id);
        MovementType movementType = MovementType.valueOf(request.getMovementType());

        int previousStock = product.getCurrentStock();
        int newStock = calculateNewStock(previousStock, request.getQuantity(), movementType);

        if (newStock < 0) {
            throw CustomException.badRequest("Insufficient stock. Available: " + previousStock);
        }

        product.setCurrentStock(newStock);
        product.setUpdatedAt(LocalDateTime.now());
        product.setUpdatedBy(getCurrentUsername());

        // Update product status based on stock
        if (newStock <= 0) {
            product.setStatus(ProductStatus.OUT_OF_STOCK);
        } else if (product.getStatus() == ProductStatus.OUT_OF_STOCK) {
            product.setStatus(ProductStatus.ACTIVE);
        }

        ProductDocument updatedProduct = productRepository.save(product);

        // Record stock movement
        recordStockMovement(updatedProduct, movementType, request.getQuantity(),
                previousStock, request.getReason(), request.getReference(),
                getCurrentUsername());

        // Check and create alerts
        stockAlertService.checkAndCreateAlerts(updatedProduct);

        log.info("Stock adjusted successfully. Previous: {}, New: {}", previousStock, newStock);
        return productMapper.toResponse(updatedProduct);
    }

    public List<StockMovementResponse> getStockHistory(String productId) {
        List<StockMovementDocument> movements = stockMovementRepository
                .findByProductIdOrderByCreatedAtDesc(productId);
        return movements.stream()
                .map(productMapper::toMovementResponse)
                .collect(Collectors.toList());
    }

    public List<ProductResponse> getLowStockProducts() {
        List<ProductDocument> products = productRepository.findLowStockProducts();
        return products.stream()
                .map(productMapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<ProductResponse> getOutOfStockProducts() {
        List<ProductDocument> products = productRepository.findOutOfStockProducts();
        return products.stream()
                .map(productMapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<ProductResponse> getProductsNeedingReorder() {
        List<ProductDocument> products = productRepository.findProductsNeedingReorder();
        return products.stream()
                .map(productMapper::toResponse)
                .collect(Collectors.toList());
    }

    public ProductStatsResponse getProductStats() {
        List<ProductDocument> allProducts = productRepository.findAll();

        long totalProducts = allProducts.size();
        long activeProducts = allProducts.stream()
                .filter(p -> p.getStatus() == ProductStatus.ACTIVE)
                .count();
        long lowStockProducts = allProducts.stream()
                .filter(ProductDocument::isLowStock)
                .count();
        long outOfStockProducts = allProducts.stream()
                .filter(ProductDocument::isOutOfStock)
                .count();
        long needingReorder = allProducts.stream()
                .filter(ProductDocument::needsReorder)
                .count();

        BigDecimal totalValue = allProducts.stream()
                .filter(p -> p.getCurrentStock() != null && p.getCostPrice() != null)
                .map(p -> p.getCostPrice().multiply(BigDecimal.valueOf(p.getCurrentStock())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return ProductStatsResponse.builder()
                .totalProducts(totalProducts)
                .activeProducts(activeProducts)
                .lowStockProducts(lowStockProducts)
                .outOfStockProducts(outOfStockProducts)
                .productsNeedingReorder(needingReorder)
                .totalInventoryValue(totalValue)
                .build();
    }

    // Helper methods

    private ProductDocument findProductById(String id) {
        return productRepository.findById(id)
                .orElseThrow(() -> CustomException.notFound("Product not found with ID: " + id));
    }

    private int calculateNewStock(int currentStock, int quantity, MovementType movementType) {
        return switch (movementType) {
            case PURCHASE, RETURN_IN, ADJUSTMENT_IN, TRANSFER_IN -> currentStock + quantity;
            case SALE, RETURN_OUT, ADJUSTMENT_OUT, TRANSFER_OUT, DAMAGED, EXPIRED -> currentStock - quantity;
        };
    }

    private void recordStockMovement(ProductDocument product, MovementType movementType,
                                     int quantity, int previousStock, String reason,
                                     String reference, String performedBy) {
        StockMovementDocument movement = StockMovementDocument.builder()
                .id(UUID.randomUUID().toString())
                .productId(product.getId())
                .productSku(product.getSku())
                .movementType(movementType)
                .quantity(quantity)
                .previousStock(previousStock)
                .newStock(product.getCurrentStock())
                .reason(reason)
                .reference(reference)
                .performedBy(performedBy)
                .createdAt(LocalDateTime.now())
                .build();

        stockMovementRepository.save(movement);
    }

    private String getCurrentUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}