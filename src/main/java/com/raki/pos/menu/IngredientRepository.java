package com.raki.pos.menu;

import com.raki.pos.model.Ingredient;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class IngredientRepository {

    private final JdbcTemplate jdbc;

    public IngredientRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static final RowMapper<Ingredient> ROW_MAPPER = new RowMapper<>() {
        @Override
        public Ingredient mapRow(ResultSet rs, int rowNum) throws SQLException {
            Ingredient i = new Ingredient();
            i.setIngredientId(rs.getLong("ingredient_id"));
            i.setIngredientCategoryId(rs.getLong("ingredient_category_id"));
            i.setName(rs.getString("name"));
            i.setPrice(rs.getBigDecimal("price"));
            return i;
        }
    };

    public List<Ingredient> findByCategory(Long categoryId) {
        String sql = """
            SELECT ingredient_id, ingredient_category_id, name, price
            FROM ingredients
            WHERE ingredient_category_id = ?
            ORDER BY name ASC
            """;
        return jdbc.query(sql, ROW_MAPPER, categoryId);
    }

    public void deleteByCategory(Long categoryId) {
        String sql = "DELETE FROM ingredients WHERE ingredient_category_id = ?";
        jdbc.update(sql, categoryId);
    }

    public void insertBatch(Long categoryId, List<Ingredient> ingredients) {
        String sql = """
            INSERT INTO ingredients (ingredient_category_id, name, price)
            VALUES (?, ?, ?)
            """;
        jdbc.batchUpdate(
                sql,
                ingredients,
                ingredients.size(),
                (ps, ing) -> {
                    ps.setLong(1, categoryId);
                    ps.setString(2, ing.getName());
                    ps.setBigDecimal(
                            3,
                            ing.getPrice() != null ? ing.getPrice() : BigDecimal.ZERO
                    );
                }
        );
    }
}
