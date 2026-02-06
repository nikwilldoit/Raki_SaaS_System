package com.raki.pos.menu;

import com.raki.pos.model.Ingredient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@Import(IngredientRepository.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql("/data.sql") // Consistent environment
class IngredientRepositoryTest {

    @Autowired
    private IngredientRepository repo;

    @Autowired
    private JdbcTemplate jdbc;

    /**
     * Create a fresh business for isolation.
     */
    private Long createTestBusiness() {
        jdbc.update("""
            INSERT INTO businesses (type, name, address, phone, is_active)
            VALUES ('BAR', 'IngrTestBiz', 'addr', '123', 'OPEN')
        """);
        return jdbc.queryForObject(
                "SELECT id FROM businesses WHERE name = 'IngrTestBiz'",
                Long.class
        );
    }

    /**
     * Helper to create a category under a fresh business.
     */
    private Long createTestCategory(String name) {
        Long businessId = createTestBusiness();
        jdbc.update("""
            INSERT INTO ingredient_categories (business_id, name, description)
            VALUES (?, ?, 'Test Description')
        """, businessId, name);

        return jdbc.queryForObject(
                "SELECT category_id FROM ingredient_categories WHERE name = ?",
                Long.class,
                name
        );
    }

    @Test
    void insertBatch_and_findByCategoryId() {
        // Arrange
        Long categoryId = createTestCategory("TestCat1");

        Ingredient ing1 = new Ingredient();
        ing1.setIngredientCategoryId(categoryId);
        ing1.setName("Cheese");
        ing1.setPrice(BigDecimal.valueOf(0.50));

        Ingredient ing2 = new Ingredient();
        ing2.setIngredientCategoryId(categoryId);
        ing2.setName("Ham");
        ing2.setPrice(BigDecimal.valueOf(0.80));

        // Act
        repo.insertBatch(categoryId, List.of(ing1, ing2));

        // Assert
        List<Ingredient> list = repo.findByCategory(categoryId);

        assertThat(list).hasSize(2);
        assertThat(list)
                .extracting(Ingredient::getName)
                .containsExactlyInAnyOrder("Cheese", "Ham");
    }

    @Test
    void deleteByCategoryId_removesAll() {
        // Arrange
        Long categoryId = createTestCategory("TestCat2");

        Ingredient ing = new Ingredient();
        ing.setIngredientCategoryId(categoryId);
        ing.setName("Mushrooms");
        ing.setPrice(BigDecimal.ZERO);

        repo.insertBatch(categoryId, List.of(ing));
        assertThat(repo.findByCategory(categoryId)).isNotEmpty();

        // Act
        repo.deleteByCategory(categoryId);

        // Assert
        assertThat(repo.findByCategory(categoryId)).isEmpty();
    }
}