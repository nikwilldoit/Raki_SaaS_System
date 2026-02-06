package com.raki.pos.discount;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDate;
import java.util.List;

@Repository
public class DiscountRepository {

    private final JdbcTemplate jdbc;

    public DiscountRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public Long createPolicy(DiscountDTO.DiscountRequest req) {
        String sql = """
            INSERT INTO discount_policies
            (business_id, name, scope, discount_type, discount_value,
             start_date, end_date, is_active)
            VALUES (?,?,?,?,?,?,?,?)
        """;

        KeyHolder kh = new GeneratedKeyHolder();

        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setObject(1, req.getBusinessId(), Types.BIGINT);
            ps.setString(2, req.getName());
            ps.setString(3, req.getScope());
            ps.setString(4, req.getDiscountType());
            ps.setBigDecimal(5, req.getDiscountValue());
            ps.setObject(6, req.getStartDate());
            ps.setObject(7, req.getEndDate());
            ps.setBoolean(8, Boolean.TRUE.equals(req.getIsActive()));
            return ps;
        }, kh);

        Number key = kh.getKey();
        return key != null ? key.longValue() : null;
    }

    public void updatePolicy(Long id, DiscountDTO.DiscountRequest req) {
        String sql = """
            UPDATE discount_policies
            SET name = ?, scope = ?, discount_type = ?, discount_value = ?,
                start_date = ?, end_date = ?, is_active = ?
            WHERE id = ? AND business_id = ?
        """;

        jdbc.update(sql,
                req.getName(),
                req.getScope(),
                req.getDiscountType(),
                req.getDiscountValue(),
                req.getStartDate(),
                req.getEndDate(),
                Boolean.TRUE.equals(req.getIsActive()),
                id,
                req.getBusinessId()
        );
    }

    public void deletePolicy(Long id, Long businessId) {
        jdbc.update("UPDATE products SET discount_id = NULL WHERE discount_id = ?", id);
        jdbc.update("UPDATE available_services SET discount_id = NULL WHERE discount_id = ?", id);
        jdbc.update("DELETE FROM discount_policies WHERE id = ? AND business_id = ?", id, businessId);
    }


    public List<DiscountDTO.DiscountResponse> findAllByBusiness(Long businessId) {
        String sql = "SELECT * FROM discount_policies WHERE business_id = ?";
        return jdbc.query(sql, (rs, rowNum) -> mapDiscount(rs), businessId);
    }

    public List<Long> findProductIdsByDiscount(Long discountId) {
        String sql = "SELECT product_id FROM products WHERE discount_id = ?";
        return jdbc.queryForList(sql, Long.class, discountId);
    }

    public void assignDiscountToProducts(Long discountId, List<Long> productIds, Long businessId) {

        jdbc.update("UPDATE products SET discount_id = NULL WHERE discount_id = ?", discountId);

        if (productIds == null || productIds.isEmpty()) return;

        String sql = "UPDATE products SET discount_id = ? WHERE product_id = ? AND business_id = ?";

        jdbc.batchUpdate(sql, productIds, productIds.size(), (ps, productId) -> {
            ps.setLong(1, discountId);
            ps.setLong(2, productId);
            ps.setLong(3, businessId);
        });
    }

    public void assignDiscountToServices(Long discountId, List<Long> serviceIds, Long businessId) {

        jdbc.update("UPDATE available_services SET discount_id = NULL WHERE discount_id = ?", discountId);
        if (serviceIds == null || serviceIds.isEmpty()) return;

        String sql = "UPDATE available_services SET discount_id = ? WHERE id = ? AND business_id = ?";
        jdbc.batchUpdate(sql, serviceIds, serviceIds.size(), (ps, serviceId) -> {
            ps.setLong(1, discountId);
            ps.setLong(2, serviceId);
            ps.setLong(3, businessId);
        });
    }


    private DiscountDTO.DiscountResponse mapDiscount(ResultSet rs) throws SQLException {
        DiscountDTO.DiscountResponse d = new DiscountDTO.DiscountResponse();
        d.setId(rs.getLong("id"));
        d.setBusinessId(rs.getLong("business_id"));
        d.setName(rs.getString("name"));
        d.setScope(rs.getString("scope"));
        d.setDiscountType(rs.getString("discount_type"));
        d.setDiscountValue(rs.getBigDecimal("discount_value"));
        d.setStartDate(rs.getObject("start_date", LocalDate.class));
        d.setEndDate(rs.getObject("end_date", LocalDate.class));
        d.setActive(rs.getBoolean("is_active"));

        LocalDate today = LocalDate.now();
        if (!d.isActive()) {
            d.setStatus("INACTIVE");
        } else if (d.getStartDate() != null && d.getStartDate().isAfter(today)) {
            d.setStatus("SCHEDULED");
        } else if (d.getEndDate() != null && d.getEndDate().isBefore(today)) {
            d.setStatus("EXPIRED");
        } else {
            d.setStatus("ACTIVE");
        }

        return d;
    }
}
