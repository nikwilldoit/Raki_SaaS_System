package com.raki.pos.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;

public class DashboardDTO {

    @Data
    @AllArgsConstructor
    public static class Response {
        private Long userId;
        private Long businessId;
        private String employeeType;
        private String businessType;
        private boolean superAdmin;
    }
}