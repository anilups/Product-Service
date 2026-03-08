package com.example.product.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.couchbase.core.mapping.Document;
import org.springframework.data.couchbase.core.mapping.Field;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document
public class StockAlertDocument {

    @Id
    private String id;

    @Field
    private String productId;

    @Field
    private String productSku;

    @Field
    private String productName;

    @Field
    private AlertType alertType;

    @Field
    private Integer currentStock;

    @Field
    private Integer threshold;

    @Field
    private AlertStatus status;

    @Field
    private LocalDateTime triggeredAt;

    @Field
    private LocalDateTime resolvedAt;

    @Field
    private String resolvedBy;

    @Field
    private String notes;
}
