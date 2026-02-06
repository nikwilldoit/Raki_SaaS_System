package com.raki.pos.reservation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Map;

public class ResDTO {

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReservationListResponseDTO {
        private int reservationId;
        private String customerName;
        private String customerPhone;
        private String serviceName;
        private String employeeName;
        private String date;
        private String status;
    }

    /**
     * Data transfer object representing a response to the request of acquiring data.
     */
    @Getter
    @Setter
    @ToString
    public static class ReservationCreationDataResponseDTO {
        private Map<String, Object> data;

        public ReservationCreationDataResponseDTO(Map<String, Object> data) {
            this.data = data;
        }
    }

    @Getter
    @Setter
    @ToString
    @AllArgsConstructor
    public static class availabilityRequestDTO {

        @NotNull(message = "Business ID is required")
        private Integer businessId;
        @NotNull(message = "Employee ID is required")
        private Integer employeeId;
        @NotBlank(message = "Date is required")
        private String date;
    }

    @Getter
    @Setter
    @ToString
    public static class ReservationCreationRequestDTO {
        @NotNull(message = "Business ID is required")
        private Integer businessId;

        @NotNull(message = "Employee ID is required")
        private Integer employeeId;

        @NotBlank(message = "Date is required")
        private String date;

        @NotBlank(message = "Time is required")
        private String customerName;

        @NotBlank(message = "Customer phone is required")
        private String customerPhone;

        @NotNull
        private Integer serviceId;

        //private String specialRequests;
    }

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReservationUpdateRequestDTO {
        @NotNull(message = "Business ID is required")
        private Integer businessId;
        private String status;
        private String customerName;
        private String customerPhone;
    }
}
