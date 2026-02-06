package com.raki.pos.payment;

import com.raki.pos.model.Payment;
import com.raki.pos.model.PaymentSplit;
import com.raki.pos.model.PaymentSplitItem;
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
public class PaymentRepository {

    private final JdbcTemplate jdbc;

    public PaymentRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public Long insertPayment(Payment p) {
        String sql = """
            INSERT INTO payments (
              business_id, order_id,
              total_amount, total_tip, payment_method,
              is_split, status
            )
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;

        KeyHolder kh = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, p.getBusinessId());
            ps.setLong(2, p.getOrderId());
            ps.setBigDecimal(3, java.math.BigDecimal.valueOf(p.getTotalAmount()));
            ps.setBigDecimal(4, java.math.BigDecimal.valueOf(p.getTotalTip()));
            ps.setString(5, p.getPaymentMethod());
            ps.setBoolean(6, p.isSplit());
            ps.setString(7, p.getStatus());
            return ps;
        }, kh);

        return Objects.requireNonNull(kh.getKey()).longValue();
    }

    public Long insertSplit(PaymentSplit s) {
        String sql = """
            INSERT INTO payment_splits (
              payment_id, payer_name,
              amount, tip_amount, payment_method, status
            )
            VALUES (?, ?, ?, ?, ?, ?)
            """;

        KeyHolder kh = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, s.getPaymentId());
            ps.setString(2, s.getPayerName());
            ps.setBigDecimal(3, java.math.BigDecimal.valueOf(s.getAmount()));
            ps.setBigDecimal(4, java.math.BigDecimal.valueOf(s.getTipAmount()));
            ps.setString(5, s.getPaymentMethod());
            ps.setString(6, s.getStatus());
            return ps;
        }, kh);

        return Objects.requireNonNull(kh.getKey()).longValue();
    }

    public void insertSplitItem(PaymentSplitItem i) {
        String sql = """
            INSERT INTO payment_split_items (
              split_id, order_item_id, amount
            )
            VALUES (?, ?, ?)
            """;
        jdbc.update(
                sql,
                i.getSplitId(),
                i.getOrderItemId(),
                java.math.BigDecimal.valueOf(i.getAmount())
        );
    }

    private static final RowMapper<PaymentDTO.PaymentSummary> PAYMENT_SUMMARY_MAPPER =
            (rs, rowNum) -> {
                PaymentDTO.PaymentSummary s = new PaymentDTO.PaymentSummary();
                s.setPaymentId(rs.getLong("payment_id"));
                s.setTotalAmount(rs.getBigDecimal("total_amount").doubleValue());
                s.setTotalTip(rs.getBigDecimal("total_tip").doubleValue());
                s.setPayerSummary(rs.getString("payer_summary"));
                s.setProductSummary(rs.getString("product_summary"));
                return s;
            };

    private static final RowMapper<Payment> PAYMENT_ROW_MAPPER = (rs, rowNum) -> {
        Payment p = new Payment();
        p.setPaymentId(rs.getLong("payment_id"));
        p.setBusinessId(rs.getLong("business_id"));
        p.setOrderId(rs.getLong("order_id"));
        p.setTotalAmount(rs.getBigDecimal("total_amount").doubleValue());
        p.setTotalTip(rs.getBigDecimal("total_tip").doubleValue());
        p.setPaymentMethod(rs.getString("payment_method"));
        p.setSplit(rs.getBoolean("is_split"));
        p.setStatus(rs.getString("status"));
        return p;
    };


    public List<PaymentDTO.PaymentSummary> findPaymentsForOrder(Long orderId) {
        String sql = """
            SELECT
              p.payment_id,
              p.total_amount,
              p.total_tip,
              GROUP_CONCAT(DISTINCT ps.payer_name SEPARATOR ', ') AS payer_summary,
              GROUP_CONCAT(DISTINCT CONCAT('Split #', ps.payment_id) SEPARATOR ', ') AS product_summary
            FROM payments p
            LEFT JOIN payment_splits ps ON ps.payment_id = p.payment_id
            WHERE p.order_id = ?
            GROUP BY p.payment_id, p.total_amount, p.total_tip
            ORDER BY p.payment_id
        """;

        return jdbc.query(sql, PAYMENT_SUMMARY_MAPPER, orderId);
    }

    public BigDecimal sumPaymentsTotalAmount(Long orderId) {
        String sql = """
        SELECT COALESCE(SUM(total_amount), 0)
        FROM payments
        WHERE order_id = ?
    """;
        return jdbc.queryForObject(sql, BigDecimal.class, orderId);
    }

    // Payments: COMPLETED για όλα τα payments του order
    public void markPaymentsCompletedForOrder(Long orderId) {
        String sql = """
        UPDATE payments
        SET status = 'COMPLETED', processed_at = NOW()
        WHERE order_id = ?
    """;
        jdbc.update(sql, orderId);
    }

    // Splits: COMPLETED για όλα τα splits των payments του order
    public void markSplitsCompletedForOrder(Long orderId) {
        String sql = """
        UPDATE payment_splits ps
        JOIN payments p ON ps.payment_id = p.payment_id
        SET ps.status = 'COMPLETED'
        WHERE p.order_id = ?
    """;
        jdbc.update(sql, orderId);
    }

    public Payment findLastCompletedByOrderId(Long orderId) {
        String sql = """
        SELECT payment_id, business_id, order_id,
               total_amount, total_tip, payment_method,
               is_split, status, created_at, processed_at
        FROM payments
        WHERE order_id = ? AND status = 'COMPLETED'
        ORDER BY created_at DESC
        LIMIT 1
        """;

        return jdbc.query(sql, PAYMENT_ROW_MAPPER, orderId)
                .stream()
                .findFirst()
                .orElse(null);
    }

}
