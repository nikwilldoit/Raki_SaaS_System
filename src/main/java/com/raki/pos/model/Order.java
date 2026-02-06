// src/main/java/com/raki/pos/model/Order.java
package com.raki.pos.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    private Long id;
    private Long businessId;
    private Long userId;
    private Long discountId;
    private String orderNumber;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private Instant orderDate;
    private String status;           // OPEN, CLOSED, CANCELLED, REFUNDED
    private String specialRequests;
}
