package com.raki.pos.menu;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductExtrasService {

    private final IngredientCategoryRepository ingredientCategoryRepository;

    public ProductExtrasService(IngredientCategoryRepository ingredientCategoryRepository) {
        this.ingredientCategoryRepository = ingredientCategoryRepository;
    }

    public List<ProductIngredientCategoryDTO> getCategoriesForProduct(Long productId) {
        return ingredientCategoryRepository.findCategoriesWithOptionsByProduct(productId);
    }
}
