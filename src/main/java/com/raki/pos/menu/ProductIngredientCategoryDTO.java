package com.raki.pos.menu;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductIngredientCategoryDTO {
    private Long categoryId;
    private String categoryName;
    private boolean singleSelect; // true για Size/Sugar, false για extras
    private List<IngredientOptionDTO> options;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class IngredientOptionDTO {
        private Long ingredientId;
        private String name;
        private BigDecimal priceDelta;
    }
}
