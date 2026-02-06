package com.raki.pos.refund;

import com.raki.pos.model.Refund;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.List;
import java.util.Objects;

@Repository
public class RefundRepository {

    private final JdbcTemplate jdbcTemplate;

    public RefundRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final RowMapper<Refund> REFUND_ROW_MAPPER = (rs, rowNum) -> {
        Refund r = new Refund();
        r.setId(rs.getLong("id"));
        r.setOrderId(rs.getLong("order_id"));
        r.setPaymentId(rs.getObject("payment_id") != null ? rs.getLong("payment_id") : null);
        r.setProcessedByUserId(
                rs.getObject("processed_by_user_id") != null ? rs.getLong("processed_by_user_id") : null
        );
        r.setAmount(rs.getBigDecimal("amount"));
        r.setReason(rs.getString("reason"));
        r.setStatus(rs.getString("status"));
        Timestamp ts = rs.getTimestamp("refunded_at");
        r.setRefundedAt(ts != null ? ts.toInstant() : null);
        return r;
    };

    // Insert refund
    public Long createRefund(Refund refund) {
        String sql = """
            INSERT INTO refunds (order_id, payment_id, processed_by_user_id, amount, reason, status)
            VALUES (?, ?, ?, ?, ?, ?)
            """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, refund.getOrderId());
            if (refund.getPaymentId() != null) {
                ps.setLong(2, refund.getPaymentId());
            } else {
                ps.setNull(2, Types.BIGINT);
            }
            if (refund.getProcessedByUserId() != null) {
                ps.setLong(3, refund.getProcessedByUserId());
            } else {
                ps.setNull(3, Types.BIGINT);
            }
            ps.setBigDecimal(4, refund.getAmount());
            ps.setString(5, refund.getReason() != null ? refund.getReason() : "");
            ps.setString(6, refund.getStatus() != null ? refund.getStatus() : "PROCESSED");
            return ps;
        }, keyHolder);

        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    // Find refunds by order
    public List<Refund> findByOrderId(Long orderId) {
        String sql = """
            SELECT id, order_id, payment_id, processed_by_user_id,
                   amount, reason, status, refunded_at
            FROM refunds
            WHERE order_id = ?
            """;
        return jdbcTemplate.query(sql, REFUND_ROW_MAPPER, orderId);
    }

    // Optional: update refund status
    public void updateStatus(Long refundId, String status) {
        String sql = "UPDATE refunds SET status = ? WHERE id = ?";
        jdbcTemplate.update(sql, status, refundId);
    }
}
