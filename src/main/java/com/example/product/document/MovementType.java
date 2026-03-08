package com.example.product.document;

public enum MovementType {
    PURCHASE,           // Stock in from supplier
    SALE,              // Stock out from sale
    RETURN_IN,         // Customer return
    RETURN_OUT,        // Return to supplier
    ADJUSTMENT_IN,     // Inventory adjustment increase
    ADJUSTMENT_OUT,    // Inventory adjustment decrease
    TRANSFER_IN,       // Transfer from another warehouse
    TRANSFER_OUT,      // Transfer to another warehouse
    DAMAGED,           // Damaged goods
    EXPIRED            // Expired goods
}