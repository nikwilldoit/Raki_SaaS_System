package com.raki.pos.product;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ProductTypeRepository {

    private final JdbcTemplate jdbc;

    public ProductTypeRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<ProductTypeDTO> findAll() {
        String sql = """
        SELECT product_type_id, product_name
        FROM product_types
        ORDER BY product_type_id
        """;

        return jdbc.query(sql, (rs, rowNum) ->
                new ProductTypeDTO(
                        rs.getLong("product_type_id"),
                        rs.getString("product_name")
                )
        );
    }
}
