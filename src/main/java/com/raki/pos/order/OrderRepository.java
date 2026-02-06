package com.raki.pos.order;

import com.raki.pos.model.Order;
import com.raki.pos.model.OrderItem;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Repository
public class OrderRepository {

    private static final RowMapper<Order> ORDER_ROW_MAPPER = (rs, rowNum) -> {
        Order o = new Order();
        o.setId(rs.getLong("id"));
        o.setBusinessId(rs.getLong("business_id"));
        o.setUserId(rs.getLong("user_id"));
        o.setDiscountId(
                rs.getObject("discount_id") != null ? rs.getLong("discount_id") : null
        );
        o.setOrderNumber(rs.getString("order_number"));
        Timestamp ts = rs.getTimestamp("order_date");
        o.setOrderDate(ts != null ? ts.toInstant() : null);
        o.setStatus(rs.getString("status"));
        o.setSpecialRequests(rs.getString("special_requests"));
        return o;
    };
    private static final RowMapper<OrderItem> ITEM_ROW_MAPPER = (rs, rowNum) -> {
        OrderItem i = new OrderItem();
        i.setOrderItemId(rs.getLong("order_item_id"));
        i.setOrderId(rs.getLong("order_id"));
        i.setProductId(rs.getLong("product_id"));
        i.setQuantity(rs.getInt("quantity"));
        i.setUnitPrice(
                rs.getBigDecimal("unit_price") != null
                        ? rs.getBigDecimal("unit_price").doubleValue()
                        : null
        );
        i.setTotalPrice(
                rs.getBigDecimal("total_price") != null
                        ? rs.getBigDecimal("total_price").doubleValue()
                        : null
        );
        i.setPaymentStatus(rs.getString("payment_status"));
        Timestamp ts = rs.getTimestamp("created_at");
        i.setCreatedAt(ts != null ? ts.toInstant() : null);
        return i;
    };
    private final JdbcTemplate jdbcTemplate;

    public OrderRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Long createOrder(Order order) {
        String sql = """
                INSERT INTO orders (business_id, user_id, discount_id,
                                    order_number, status, special_requests)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(con -> {
            PreparedStatement ps =
                    con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, order.getBusinessId());
            ps.setLong(2, order.getUserId());
            if (order.getDiscountId() != null) {
                ps.setLong(3, order.getDiscountId());
            }
            else {
                ps.setNull(3, Types.BIGINT);
            }
            ps.setString(4, order.getOrderNumber());
            ps.setString(5, order.getStatus());
            ps.setString(6, order.getSpecialRequests());
            return ps;
        }, keyHolder);

        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    public void addOrderItem(OrderItem item) {
        int quantity = item.getQuantity() != null ? item.getQuantity() : 1;
        double unitPrice = item.getUnitPrice() != null ? item.getUnitPrice() : 0.0;
        double totalPrice = item.getTotalPrice() != null
                ? item.getTotalPrice()
                : unitPrice * quantity;

        String sql = """
                INSERT INTO order_items (
                  order_id, product_id, quantity,
                  unit_price, total_price,
                  payment_status
                )
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        jdbcTemplate.update(sql,
                item.getOrderId(),
                item.getProductId(),
                quantity,
                BigDecimal.valueOf(unitPrice),
                BigDecimal.valueOf(totalPrice),
                item.getPaymentStatus()
        );
    }

    public List<OrderItem> findItemsByOrderId(Long orderId) {
        String sql = """
                SELECT order_item_id,
                       order_id,
                       product_id,
                       quantity,
                       unit_price,
                       total_price,
                       payment_status,
                       created_at
                FROM order_items
                WHERE order_id = ?
                """;
        return jdbcTemplate.query(sql, ITEM_ROW_MAPPER, orderId);
    }

    public Order findById(Long id) {
        String sql = """
                  SELECT id, business_id, user_id, discount_id,
                         order_number, order_date, status, special_requests
                  FROM orders
                  WHERE id = ?
                """;
        return jdbcTemplate.queryForObject(sql, ORDER_ROW_MAPPER, id);
    }

    public List<Order> findByBusiness(Long businessId) {
        String sql = """
                    SELECT id, business_id, user_id, discount_id,
                           order_number, order_date, status, special_requests
                    FROM orders
                    WHERE business_id = ?
                    ORDER BY order_date DESC
                """;

        return jdbcTemplate.query(sql, ORDER_ROW_MAPPER, businessId);
    }

    public void update(Order order) {
        String sql = "UPDATE orders SET status = ? WHERE id = ?";
        jdbcTemplate.update(sql, order.getStatus(), order.getId());
    }

    // mark συγκεκριμένα items ως PAID (για split & άλλες χρήσεις)
    public void markOrderItemsPaid(Collection<Long> orderItemIds) {
        if (orderItemIds == null || orderItemIds.isEmpty()) {
            return;
        }

        String placeholders = orderItemIds.stream()
                .map(id -> "?")
                .collect(Collectors.joining(","));

        String sql = """
                    UPDATE order_items
                    SET payment_status = 'PAID'
                    WHERE order_item_id IN (%s)
                """.formatted(placeholders);

        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql);
            int i = 1;
            for (Long id : orderItemIds) {
                ps.setLong(i++, id);
            }
            return ps;
        });
    }

    public BigDecimal sumOrderItemsTotal(Long orderId) {
        String sql = """
                    SELECT COALESCE(SUM(oi.total_price), 0)
                    FROM order_items oi
                    WHERE oi.order_id = ?
                """;
        return jdbcTemplate.queryForObject(sql, BigDecimal.class, orderId);
    }

    // change status order (OPEN -> CLOSED)
    public void updateStatus(Long orderId, String status) {
        String sql = "UPDATE orders SET status = ? WHERE id = ?";
        jdbcTemplate.update(sql, status, orderId);
    }

    // unpaid items for the next split
    public List<OrderItem> findUnpaidItems(Long orderId) {
        String sql = """
                    SELECT order_item_id,
                           order_id,
                           product_id,
                           quantity,
                           unit_price,
                           total_price,
                           payment_status,
                           created_at
                    FROM order_items
                    WHERE order_id = ? AND payment_status = 'UNPAID'
                """;
        return jdbcTemplate.query(sql, ITEM_ROW_MAPPER, orderId);
    }

    // changes status to PAID in every item of an order
    public void markAllItemsPaidForOrder(Long orderId) {
        String sql = """
                    UPDATE order_items
                    SET payment_status = 'PAID'
                    WHERE order_id = ?
                """;
        jdbcTemplate.update(sql, orderId);
    }

}
