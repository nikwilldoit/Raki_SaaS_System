package com.raki.pos.menu;

import com.raki.pos.model.Ingredient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/menu/ingredient-categories")
@CrossOrigin(origins = "http://localhost:3000")
public class IngredientCategoryController {

    private final IngredientCategoryService service;

    public IngredientCategoryController(IngredientCategoryService service) {
        this.service = service;
    }

    @GetMapping
    public List<IngredientCategoryListDTO> getCategories(@RequestParam Long businessId) {
        return service.getCategoriesForBusiness(businessId);
    }

    @GetMapping("/{id}/ingredients")
    public List<Ingredient> listIngredients(@PathVariable Long id) {
        return service.listIngredients(id);
    }

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody IngredientCategoryRequest request) {
        Long id = service.saveCategoryWithIngredients(request);
        return ResponseEntity.ok(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> update(@PathVariable Long id,
                                       @RequestBody IngredientCategoryRequest request) {
        request.setCategoryId(id);
        Long updatedId = service.saveCategoryWithIngredients(request);
        return ResponseEntity.ok(updatedId);
    }


    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id,
                       @RequestParam Long businessId) {
        service.deleteCategory(id, businessId);
    }
}
