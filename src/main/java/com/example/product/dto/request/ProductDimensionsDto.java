package com.example.product.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDimensionsDto {
    private Double length;
    private Double width;
    private Double height;
    private String unit;
}