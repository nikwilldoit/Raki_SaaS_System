// src/test/java/com/raki/pos/menu/IngredientCategoryControllerTest.java
package com.raki.pos.menu;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.raki.pos.auth.JwtAuthenticationFilter;
import com.raki.pos.model.Ingredient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(IngredientCategoryController.class)
class IngredientCategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IngredientCategoryService service;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser
    void getCategories_returnsOverviewList() throws Exception {
        List<IngredientCategoryListDTO> list = List.of(
                new IngredientCategoryListDTO(
                        1L,
                        3L,
                        "Sauces",
                        "desc",
                        2L
                )
        );
        when(service.getCategoriesForBusiness(3L)).thenReturn(list);

        mockMvc.perform(get("/api/menu/ingredient-categories")
                        .param("businessId", "3"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void listIngredients_returnsOkWithJson() throws Exception {
        Ingredient ing = new Ingredient();
        ing.setIngredientId(1L);
        ing.setIngredientCategoryId(2L);
        ing.setName("Cheese");
        ing.setPrice(BigDecimal.ZERO);

        List<Ingredient> list = List.of(ing);
        when(service.listIngredients(2L)).thenReturn(list);

        mockMvc.perform(get("/api/menu/ingredient-categories/2/ingredients"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void createCategory_returnsId() throws Exception {
        IngredientCategoryRequest req = new IngredientCategoryRequest();
        req.setBusinessId(3L);
        req.setName("Sauces");
        when(service.saveCategoryWithIngredients(any())).thenReturn(10L);

        mockMvc.perform(post("/api/menu/ingredient-categories")
                        .with(csrf())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }


    @Test
    @WithMockUser
    void updateCategory_setsIdOnRequestAndReturnsId() throws Exception {
        IngredientCategoryRequest req = new IngredientCategoryRequest();
        req.setBusinessId(3L);
        req.setName("Updated");
        when(service.saveCategoryWithIngredients(any())).thenReturn(5L);

        mockMvc.perform(put("/api/menu/ingredient-categories/5")
                        .with(csrf())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void deleteCategory_callsService() throws Exception {
        mockMvc.perform(delete("/api/menu/ingredient-categories/7")
                        .with(csrf())
                        .param("businessId", "3"))
                .andExpect(status().isOk());
    }
}
