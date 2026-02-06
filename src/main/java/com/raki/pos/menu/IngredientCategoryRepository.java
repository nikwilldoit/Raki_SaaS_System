package com.raki.pos.menu;

import com.raki.pos.model.IngredientCategory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.*;
import java.util.*;

@Repository
public class IngredientCategoryRepository {

    private final JdbcTemplate jdbc;

    public IngredientCategoryRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static final RowMapper<IngredientCategory> ROW_MAPPER = (rs, rowNum) -> {
        IngredientCategory c = new IngredientCategory();
        c.setCategoryId(rs.getLong("category_id"));
        c.setBusinessId(rs.getLong("business_id"));
        c.setName(rs.getString("name"));
        c.setDescription(rs.getString("description"));
        return c;
    };

    public List<IngredientCategory> findByBusiness(Long businessId) {
        String sql = """
            SELECT category_id, business_id, name, description
            FROM ingredient_categories
            WHERE business_id = ?
            ORDER BY name ASC
            """;
        return jdbc.query(sql, ROW_MAPPER, businessId);
    }

    public Long insert(IngredientCategory category) {
        String sql = """
            INSERT INTO ingredient_categories (business_id, name, description)
            VALUES (?, ?, ?)
            """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update(con -> {
            PreparedStatement ps =
                    con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, category.getBusinessId());
            ps.setString(2, category.getName());
            ps.setString(3, category.getDescription());
            return ps;
        }, keyHolder);

        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    public void update(IngredientCategory c) {
        String sql = """
            UPDATE ingredient_categories
            SET name = ?, description = ?
            WHERE category_id = ? AND business_id = ?
            """;
        jdbc.update(
                sql,
                c.getName(),
                c.getDescription(),
                c.getCategoryId(),
                c.getBusinessId()
        );
    }

    public void delete(Long categoryId, Long businessId) {
        String sql = """
            DELETE FROM ingredient_categories
            WHERE category_id = ? AND business_id = ?
            """;
        jdbc.update(sql, categoryId, businessId);
    }

    // in IngredientCategoryRepository

    public List<IngredientCategoryListDTO> findOverviewByBusiness(Long businessId) {
        String sql = """
        SELECT 
            c.category_id,
            c.business_id,
            c.name,
            c.description,
            COUNT(DISTINCT i.ingredient_id)      AS ingredients_count
        FROM ingredient_categories c
        LEFT JOIN ingredients i 
            ON i.ingredient_category_id = c.category_id
        WHERE c.business_id = ?
        GROUP BY 
            c.category_id,
            c.business_id,
            c.name,
            c.description
        ORDER BY c.name ASC
        """;

        return jdbc.query(sql, (rs, rowNum) ->
                        new IngredientCategoryListDTO(
                                rs.getLong("category_id"),
                                rs.getLong("business_id"),
                                rs.getString("name"),
                                rs.getString("description"),
                                rs.getLong("ingredients_count")
                        ),
                businessId
        );
    }


    public List<ProductIngredientCategoryDTO> findCategoriesWithOptionsByProduct(Long productId) {
        String sql = """
            SELECT ic.category_id,
                   ic.name        AS category_name,
                   ic.description AS category_desc,
                   i.ingredient_id,
                   i.name         AS ingredient_name,
                   i.price        AS price_delta
            FROM product_ingredients pi
            JOIN ingredients i
              ON i.ingredient_id = pi.ingredient_id
            JOIN ingredient_categories ic
              ON ic.category_id = i.ingredient_category_id
            WHERE pi.product_id = ?
            ORDER BY ic.category_id, i.name
            """;

        return jdbc.query(sql, rs -> {
            Map<Long, ProductIngredientCategoryDTO> map = new LinkedHashMap<>();
            while (rs.next()) {
                Long catId = rs.getLong("category_id");
                ProductIngredientCategoryDTO cat = map.computeIfAbsent(catId, id -> {
                    ProductIngredientCategoryDTO dto = new ProductIngredientCategoryDTO();
                    dto.setCategoryId(id);
                    try {
                        dto.setCategoryName(rs.getString("category_name"));
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    // description = 'single' => singleSelect
                    String desc = null;
                    try {
                        desc = rs.getString("category_desc");
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    dto.setSingleSelect("single".equalsIgnoreCase(desc));
                    dto.setOptions(new ArrayList<>());
                    return dto;
                });

                Long ingId = rs.getLong("ingredient_id");
                if (!rs.wasNull()) {
                    ProductIngredientCategoryDTO.IngredientOptionDTO opt =
                            new ProductIngredientCategoryDTO.IngredientOptionDTO(
                                    ingId,
                                    rs.getString("ingredient_name"),
                                    rs.getBigDecimal("price_delta") != null
                                            ? rs.getBigDecimal("price_delta")
                                            : BigDecimal.ZERO
                            );
                    cat.getOptions().add(opt);
                }
            }
            return new ArrayList<>(map.values());
        }, productId);
    }



}
