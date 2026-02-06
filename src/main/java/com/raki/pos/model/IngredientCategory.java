// src/main/java/com/raki/pos/model/IngredientCategory.java
package com.raki.pos.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IngredientCategory {
    private Long categoryId;
    private Long productId;     // products.product_id
    private Long businessId;    // businesses.id
    private String name;
    private String description;
}
