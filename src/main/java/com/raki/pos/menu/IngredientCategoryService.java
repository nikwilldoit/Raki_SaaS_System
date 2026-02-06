package com.raki.pos.menu;

import com.raki.pos.model.Ingredient;
import com.raki.pos.model.IngredientCategory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class IngredientCategoryService {

    private final IngredientCategoryRepository catRepo;
    private final IngredientRepository ingRepo;

    public IngredientCategoryService(IngredientCategoryRepository catRepo,
                                     IngredientRepository ingRepo) {
        this.catRepo = catRepo;
        this.ingRepo = ingRepo;
    }

    public List<IngredientCategory> listByBusiness(Long businessId) {
        return catRepo.findByBusiness(businessId);
    }

    public List<Ingredient> listIngredients(Long categoryId) {
        return ingRepo.findByCategory(categoryId);
    }

    public Long saveCategoryWithIngredients(IngredientCategoryRequest req) {
        IngredientCategory c = new IngredientCategory(
                req.getCategoryId(),
                req.getProductId(),
                req.getBusinessId(),
                req.getName(),
                req.getDescription()
        );

        Long categoryId;
        if (c.getCategoryId() == null) {
            categoryId = catRepo.insert(c);
        } else {
            catRepo.update(c);
            categoryId = c.getCategoryId();
            ingRepo.deleteByCategory(categoryId);
        }

        List<Ingredient> ingredients =
                req.getIngredients() == null ? List.of() :
                        req.getIngredients().stream()
                                .filter(i -> i.getName() != null && !i.getName().isBlank())
                                .map(i -> new Ingredient(
                                        null,
                                        categoryId,
                                        i.getName(),
                                        i.getPrice()
                                ))
                                .collect(Collectors.toList());

        if (!ingredients.isEmpty()) {
            ingRepo.insertBatch(categoryId, ingredients);
        }

        return categoryId;
    }

    public void deleteCategory(Long categoryId, Long businessId) {
        catRepo.delete(categoryId, businessId);
    }

    // IngredientCategoryService
    public List<IngredientCategoryListDTO> getCategoriesForBusiness(Long businessId) {
        return catRepo.findOverviewByBusiness(businessId);
    }

}
