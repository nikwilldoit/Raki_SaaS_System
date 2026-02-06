package com.raki.pos.payment;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PaymentDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreatePaymentRequest {
        private Long businessId;
        private Long orderId;
        private double totalAmount;
        private double totalTip;
        private String paymentMethod;   // CASH / CARD / GIFT_CARD
        private boolean split;
        private List<SplitDTO> splits;  // empty for non-split payments
        private BigDecimal discountPercent;
        private BigDecimal discountAmount;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SplitDTO {
        private String payerName;
        private double amount;
        private double tipAmount;
        private String paymentMethod;
        private List<SplitItemDTO> items;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SplitItemDTO {
        private Long orderItemId;
        private double amount;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentSummary {
        private Long paymentId;
        private double totalAmount;
        private double totalTip;
        private String payerSummary;
        private String productSummary;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentResult {
        private boolean orderCompleted;

        // For non-completed payments
        private List<?> remainingItems;
        private BigDecimal subtotal;
        private BigDecimal discountPercent;
        private BigDecimal discountAmount;
        private BigDecimal total;
    }
}
