package com.example.product.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDimensions {
    private Double length;
    private Double width;
    private Double height;
    private String unit; // cm, inch, etc.
}