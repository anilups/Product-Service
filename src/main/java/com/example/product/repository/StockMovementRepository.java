package com.example.product.repository;


import com.example.product.document.MovementType;
import com.example.product.document.StockMovementDocument;
import org.springframework.data.couchbase.repository.CouchbaseRepository;
import org.springframework.data.couchbase.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StockMovementRepository extends CouchbaseRepository<StockMovementDocument, String> {
    List<StockMovementDocument> findByProductId(String productId);
    List<StockMovementDocument> findByProductIdOrderByCreatedAtDesc(String productId);
    List<StockMovementDocument> findByMovementType(MovementType movementType);

    @Query("#{#n1ql.selectEntity} WHERE #{#n1ql.filter} AND productId = $1 AND createdAt BETWEEN $2 AND $3")
    List<StockMovementDocument> findByProductIdAndDateRange(String productId, LocalDateTime start, LocalDateTime end);
}