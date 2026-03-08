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
public class StockMovementDocument {

    @Id
    private String id;

    @Field
    private String productId;

    @Field
    private String productSku;

    @Field
    private MovementType movementType;

    @Field
    private Integer quantity;

    @Field
    private Integer previousStock;

    @Field
    private Integer newStock;

    @Field
    private String reason;

    @Field
    private String reference; // PO number, invoice number, etc.

    @Field
    private String performedBy;

    @Field
    private LocalDateTime createdAt;

    @Field
    private String notes;
}
