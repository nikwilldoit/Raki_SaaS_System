package com.raki.pos.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentSplitItem {
    private Long splitItemId;
    private Long splitId;
    private Long orderItemId;
    private double amount;
}
