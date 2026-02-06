package com.raki.pos.menu;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class IngredientCategoryListDTO {
    private Long categoryId;
    private Long businessId;
    private String name;
    private String description;
    private long ingredientsCount;
}
