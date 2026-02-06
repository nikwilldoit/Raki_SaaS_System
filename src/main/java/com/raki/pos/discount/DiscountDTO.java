package com.raki.pos.discount;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class DiscountDTO {

    @Data
    public static class DiscountResponse {
        private Long id;
        private Long businessId;
        private String name;
        private String scope;
        private String discountType;
        private BigDecimal discountValue;
        private LocalDate startDate;
        private LocalDate endDate;
        private boolean active;
        private String status;
    }

    @Data
    public static class DiscountRequest {
        private Long businessId;
        private String name;
        private String scope;
        private String discountType;
        private BigDecimal discountValue;
        private LocalDate startDate;
        private LocalDate endDate;
        private Boolean isActive;


        private List<Long> productIds;
        private List<Long> serviceIds;
    }
}
