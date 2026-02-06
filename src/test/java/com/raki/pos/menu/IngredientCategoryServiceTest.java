// src/test/java/com/raki/pos/menu/IngredientCategoryServiceTest.java
package com.raki.pos.menu;

import com.raki.pos.model.Ingredient;
import com.raki.pos.model.IngredientCategory;
import com.raki.pos.menu.IngredientCategoryRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IngredientCategoryServiceTest {

    @Mock
    private IngredientCategoryRepository categoryRepository;

    @Mock
    private IngredientRepository ingredientRepository;

    @InjectMocks
    private IngredientCategoryService service;

    @Test
    void getCategoriesForBusiness_returnsDtoListFromRepo() {
        Long businessId = 3L;
        List<IngredientCategoryListDTO> overview = List.of(
                new IngredientCategoryListDTO(
                        1L,
                        businessId,
                        "Cat1",
                        "Desc1",
                        2L    // ingredientsCount
                )
        );
        when(categoryRepository.findOverviewByBusiness(businessId)).thenReturn(overview);

        var result = service.getCategoriesForBusiness(businessId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCategoryId()).isEqualTo(1L);
        assertThat(result.get(0).getName()).isEqualTo("Cat1");
        verify(categoryRepository).findOverviewByBusiness(businessId);
    }

    @Test
    void listByBusiness_returnsCategoriesFromRepo() {
        Long businessId = 3L;
        List<IngredientCategory> categories = List.of(
                new IngredientCategory(1L, null, businessId, "Cat1", "Desc1")
        );
        when(categoryRepository.findByBusiness(businessId)).thenReturn(categories);

        List<IngredientCategory> result = service.listByBusiness(businessId);

        assertThat(result).isEqualTo(categories);
        verify(categoryRepository).findByBusiness(businessId);
    }

    @Test
    void saveCategoryWithIngredients_insertsCategoryAndIngredients() {
        IngredientCategoryRequest req = new IngredientCategoryRequest();
        req.setCategoryId(null);
        req.setBusinessId(3L);
        req.setProductId(null);
        req.setName("Sauces");
        req.setDescription("Sauce options");

        IngredientCategoryRequest.IngredientDto ing1 = new IngredientCategoryRequest.IngredientDto();
        ing1.setName("Ketchup");
        ing1.setPrice(BigDecimal.valueOf(0.50));

        IngredientCategoryRequest.IngredientDto ing2 = new IngredientCategoryRequest.IngredientDto();
        ing2.setName("Mayo");
        ing2.setPrice(BigDecimal.valueOf(0.40));

        req.setIngredients(List.of(ing1, ing2));

        when(categoryRepository.insert(any())).thenReturn(10L);

        Long id = service.saveCategoryWithIngredients(req);

        assertThat(id).isEqualTo(10L);
        verify(categoryRepository).insert(any(IngredientCategory.class));

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<Ingredient>> captor = ArgumentCaptor.forClass(List.class);
        verify(ingredientRepository).insertBatch(eq(10L), captor.capture());

        List<Ingredient> saved = captor.getValue();
        assertThat(saved).hasSize(2);
        assertThat(saved.get(0).getName()).isEqualTo("Ketchup");
    }


    @Test
    void saveCategoryWithEmptyIngredients_updatesAndClearsOnly() {
        IngredientCategoryRequest req = new IngredientCategoryRequest();
        req.setCategoryId(5L);
        req.setBusinessId(3L);
        req.setProductId(null);
        req.setName("Updated");
        req.setDescription("Desc");
        req.setIngredients(List.of()); // κενό

        Long id = service.saveCategoryWithIngredients(req);

        assertThat(id).isEqualTo(5L);
        verify(categoryRepository).update(any(IngredientCategory.class));
        verify(ingredientRepository).deleteByCategory(5L);
        verify(ingredientRepository, never()).insertBatch(anyLong(), anyList());
    }

    @Test
    void deleteCategory_delegatesToRepository() {
        service.deleteCategory(7L, 3L);
        verify(categoryRepository).delete(7L, 3L);
    }

    @Test
    void listIngredients_returnsListFromRepo() {
        Long categoryId = 2L;

        Ingredient ing = new Ingredient();
        ing.setIngredientId(1L);
        ing.setIngredientCategoryId(categoryId);
        ing.setName("Cheese");
        ing.setPrice(BigDecimal.ZERO);

        List<Ingredient> list = List.of(ing);
        when(ingredientRepository.findByCategory(categoryId)).thenReturn(list);

        List<Ingredient> result = service.listIngredients(categoryId);

        assertThat(result).isEqualTo(list);
        verify(ingredientRepository).findByCategory(categoryId);
    }
}
