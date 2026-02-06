package com.raki.pos.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequest {

    private Long businessId;
    private Long userId;
    private String orderNumber;
    private String specialRequests;

    private List<OrderLine> items;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderLine {
        private Long productId;
        private Integer quantity;
        private Double unitPrice;
        private Double totalPrice;
    }
}
