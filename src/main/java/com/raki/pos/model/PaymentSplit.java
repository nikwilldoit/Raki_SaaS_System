package com.raki.pos.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentSplit {
    private Long splitId;
    private Long paymentId;
    private String payerName;
    private double amount;
    private double tipAmount;
    private String paymentMethod;
    private String status;

}