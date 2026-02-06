// src/main/java/com/raki/pos/model/Ingredient.java
package com.raki.pos.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ingredient {
    private Long ingredientId;
    private Long ingredientCategoryId;
    private String name;
    private BigDecimal price;
}
