package com.raki.pos.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Refund {
    private Long id;
    private Long orderId;
    private Long paymentId;
    private Long processedByUserId;
    private BigDecimal amount;
    private String reason;
    private String status = "PROCESSED";
    private Instant refundedAt;
}
