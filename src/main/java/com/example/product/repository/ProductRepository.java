package com.example.product.repository;


import com.example.product.document.ProductDocument;
import com.example.product.document.ProductStatus;
import org.springframework.data.couchbase.repository.CouchbaseRepository;
import org.springframework.data.couchbase.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends CouchbaseRepository<ProductDocument, String> {
    Optional<ProductDocument> findBySku(String sku);
    boolean existsBySku(String sku);
    List<ProductDocument> findByStatus(ProductStatus status);
    Page<ProductDocument> findByCategory(String category, Pageable pageable);

    @Query("#{#n1ql.selectEntity} WHERE #{#n1ql.filter} AND currentStock <= minStockLevel AND status = 'ACTIVE'")
    List<ProductDocument> findLowStockProducts();

    @Query("#{#n1ql.selectEntity} WHERE #{#n1ql.filter} AND currentStock <= 0 AND status = 'ACTIVE'")
    List<ProductDocument> findOutOfStockProducts();

    @Query("#{#n1ql.selectEntity} WHERE #{#n1ql.filter} AND currentStock <= reorderPoint AND status = 'ACTIVE'")
    List<ProductDocument> findProductsNeedingReorder();

    @Query("#{#n1ql.selectEntity} WHERE #{#n1ql.filter} AND LOWER(name) LIKE '%' || LOWER($1) || '%' OR LOWER(sku) LIKE '%' || LOWER($1) || '%'")
    List<ProductDocument> searchProducts(String keyword);
}
