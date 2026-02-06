// src/main/java/com/raki/pos/menu/dto/IngredientCategoryRequest.java
package com.raki.pos.menu;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IngredientCategoryRequest {
    private Long categoryId;
    private Long productId;     // products.product_id
    private Long businessId;
    private String name;
    private String description;
    private List<IngredientDto> ingredients;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class IngredientDto {
        private Long ingredientId;
        private String name;
        private BigDecimal price;
    }
}
