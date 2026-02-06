// src/main/java/com/raki/pos/model/OrderItem.java
package com.raki.pos.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {
    private Long orderItemId;
    private Long orderId;
    private Long productId;
    private Integer quantity;
    private Double unitPrice;
    private Double totalPrice;
    private String paymentStatus;    // UNPAID / PAID
    private Instant createdAt;

    public OrderItem(Long orderItemId,
                     Long orderId,
                     Long productId,
                     Integer quantity,
                     String paymentStatus,
                     Double totalPrice) {
        this.orderItemId = orderItemId;
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
        this.paymentStatus = paymentStatus;
        this.totalPrice = totalPrice;
        this.unitPrice = null;
        this.createdAt = null;
    }
}
