package com.raki.pos.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    private Long paymentId;
    private Long businessId;
    private Long orderId;
    private double totalAmount;
    private double totalTip;
    private String paymentMethod;
    private boolean isSplit;
    private String status;
}
