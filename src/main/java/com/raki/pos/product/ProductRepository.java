package com.raki.pos.product;

import com.raki.pos.model.Product;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.*;
import java.util.List;
import java.util.Objects;

@Repository
public class ProductRepository {

    private final JdbcTemplate jdbc;

    public ProductRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static final RowMapper<Product> ROW_MAPPER = (rs, rowNum) -> {
        Product p = new Product();
        p.setProductId(rs.getLong("product_id"));
        p.setName(rs.getString("name"));
        p.setDescription(rs.getString("description"));
        p.setTaxCategory(rs.getLong("tax_category"));
        p.setProductType(rs.getLong("product_type"));
        p.setProductTypeName(rs.getString("product_type_name"));
        p.setBusinessId(rs.getLong("business_id"));
        p.setBasePrice(rs.getBigDecimal("base_price"));
        p.setDiscountId(rs.getObject("discount_id", Long.class));
        p.setType(rs.getString("type"));
        p.setStatus(rs.getString("status"));

        Timestamp c = rs.getTimestamp("created_at");
        Timestamp u = rs.getTimestamp("updated_at");
        p.setCreatedAt(c != null ? c.toInstant() : null);
        p.setUpdatedAt(u != null ? u.toInstant() : null);

        // ----- PRICE PIPELINE -----

        BigDecimal basePrice = p.getBasePrice();
        BigDecimal taxedPrice = basePrice;
        BigDecimal finalPrice = basePrice;

        // 1️⃣ Apply Tax First
        BigDecimal taxRate = rs.getBigDecimal("tax_rate"); // ← FIXED NAME
        if (taxRate != null) {
            taxedPrice = basePrice.multiply(
                    BigDecimal.ONE.add(taxRate.divide(BigDecimal.valueOf(100)))
            );
        }

        // 2️⃣ Apply Discount After Tax
        finalPrice = taxedPrice;

        Long discountId = p.getDiscountId();
        if (discountId != null) {
            String discountType = rs.getString("discount_type");
            BigDecimal discountValue = rs.getBigDecimal("discount_value");

            if (discountValue != null) {
                if ("PERCENT".equalsIgnoreCase(discountType)) {
                    finalPrice = taxedPrice.subtract(
                            taxedPrice.multiply(discountValue)
                                    .divide(BigDecimal.valueOf(100))
                    );
                } else if ("AMOUNT".equalsIgnoreCase(discountType)) {
                    finalPrice = taxedPrice.subtract(discountValue);
                    if (finalPrice.compareTo(BigDecimal.ZERO) < 0)
                        finalPrice = BigDecimal.ZERO;
                }
            }
        }

        // Return final calculated price
        p.setBasePrice(finalPrice);

        return p;
    };

    public List<Product> findActiveByBusiness(Long businessId) {
        String sql = """
            SELECT 
                p.product_id, p.name, p.description, p.tax_category,
                p.product_type, pt.product_name AS product_type_name,
                p.business_id, p.base_price, p.discount_id,
                d.discount_type, d.discount_value,
                t.rate AS tax_rate,
                p.type, p.status, p.created_at, p.updated_at
            FROM products p
            JOIN product_types pt ON pt.product_type_id = p.product_type
            LEFT JOIN discount_policies d ON d.id = p.discount_id
            JOIN tax_policies t ON t.id = p.tax_category
            WHERE p.business_id = ? AND p.status = 'ACTIVE'
            ORDER BY p.name ASC
        """;

        return jdbc.query(sql, ROW_MAPPER, businessId);
    }

    public Product findById(Long id) {
        String sql = """
            SELECT 
                p.product_id, p.name, p.description, p.tax_category,
                p.product_type, pt.product_name AS product_type_name,
                p.business_id, p.base_price, p.discount_id,
                d.discount_type, d.discount_value,
                t.rate AS tax_rate,
                p.type, p.status, p.created_at, p.updated_at
            FROM products p
            JOIN product_types pt ON pt.product_type_id = p.product_type
            LEFT JOIN discount_policies d ON d.id = p.discount_id
            JOIN tax_policies t ON t.id = p.tax_category
            WHERE p.product_id = ?
        """;

        return jdbc.queryForObject(sql, ROW_MAPPER, id);
    }

    public Long insert(Product p) {
        String sql = """
            INSERT INTO products
            (name, description, product_image, tax_category,
             product_type, business_id, base_price, discount_id, type, status)
            VALUES (?, ?, NULL, ?, ?, ?, ?, ?, ?, ?)
        """;

        KeyHolder kh = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, new String[]{"product_id"});
            ps.setString(1, p.getName());
            ps.setString(2, p.getDescription());
            ps.setLong(3, p.getTaxCategory());
            ps.setLong(4, p.getProductType());
            ps.setLong(5, p.getBusinessId());
            ps.setBigDecimal(6, p.getBasePrice() != null ? p.getBasePrice() : BigDecimal.ZERO);
            ps.setObject(7, p.getDiscountId());
            ps.setString(8, p.getType() != null ? p.getType() : "PRODUCT");
            ps.setString(9, p.getStatus() != null ? p.getStatus() : "ACTIVE");
            return ps;
        }, kh);

        return Objects.requireNonNull(kh.getKey()).longValue();
    }

    public void update(Product p) {
        String sql = """
            UPDATE products
            SET name = ?, description = ?, tax_category = ?,
                product_type = ?, base_price = ?, discount_id = ?,
                type = ?, status = ?
            WHERE product_id = ? AND business_id = ?
        """;

        jdbc.update(sql,
                p.getName(), p.getDescription(), p.getTaxCategory(),
                p.getProductType(), p.getBasePrice(), p.getDiscountId(),
                p.getType(), p.getStatus(), p.getProductId(), p.getBusinessId());
    }

    public void delete(Long productId, Long businessId) {
        jdbc.update("DELETE FROM products WHERE product_id = ? AND business_id = ?", productId, businessId);
    }

    public void deleteIngredientsByProduct(Long productId) {
        jdbc.update("DELETE FROM product_ingredients WHERE product_id = ?", productId);
    }

    public void insertProductIngredients(Long productId, List<Long> ingredientIds) {
        String sql = "INSERT INTO product_ingredients (product_id, ingredient_id) VALUES (?, ?)";
        jdbc.batchUpdate(sql, ingredientIds, ingredientIds.size(),
                (ps, ingredientId) -> {
                    ps.setLong(1, productId);
                    ps.setLong(2, ingredientId);
                });
    }
}
