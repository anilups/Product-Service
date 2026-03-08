package com.example.product.repository;


import com.example.product.document.AlertStatus;
import com.example.product.document.AlertType;
import com.example.product.document.StockAlertDocument;
import org.springframework.data.couchbase.repository.CouchbaseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StockAlertRepository extends CouchbaseRepository<StockAlertDocument, String> {
    List<StockAlertDocument> findByStatus(AlertStatus status);
    List<StockAlertDocument> findByAlertType(AlertType alertType);
    List<StockAlertDocument> findByProductId(String productId);
    Optional<StockAlertDocument> findByProductIdAndStatusAndAlertType(String productId, AlertStatus status, AlertType alertType);

    List<StockAlertDocument> findByStatusOrderByTriggeredAtDesc(AlertStatus status);
}
