// src/main/java/com/raki/pos/model/Product.java
package com.raki.pos.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    private Long productId;
    private String name;
    private String description;
    private Long taxCategory;      // tax_policies.id
    private Long productType;      // product_types.product_type_id
    private String productTypeName; //product_types.product_name
    private Long businessId;       // businesses.id
    private BigDecimal basePrice;
    private Long discountId;
    private String type;           // PRODUCT / SERVICE
    private String status;         // ACTIVE / INACTIVE
    private Instant createdAt;
    private Instant updatedAt;
    private List<Long> ingredientIds;

    // Optional: discount value (calculated)
    private BigDecimal discountedPrice;
}
