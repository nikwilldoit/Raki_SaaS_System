package com.raki.pos.reservation;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/reservations")
public class ResController {

    private static final Logger logger = LogManager.getLogger(ResController.class);

    private final ResService resService;

    public ResController(ResService resService) {
        this.resService = resService;
    }

    @GetMapping()
    public ResponseEntity<Object> getReservations(@RequestParam @Min(1) Integer businessId) {
        logger.info("GET /api/reservations request received");
        return ResponseEntity.ok(resService.getReservations(businessId));
    }

    @GetMapping("/new")
    public ResponseEntity<Object> getBusinessReservationData(@RequestParam @Min(1) Integer businessId) {
        logger.info("GET /api/reservations/new request received");

        ResDTO.ReservationCreationDataResponseDTO resDTO = resService.getBusinessReservationData(businessId);
        return ResponseEntity.ok(resDTO);
    }

    @GetMapping("/availability")
    public ResponseEntity<Object> getAvailability(@Valid ResDTO.availabilityRequestDTO requestDTO) {
        logger.debug("GET /api/reservations/availability request received with params: {}", requestDTO.toString());

        List<String> availabilityData = resService.fetchAvailability(
                requestDTO.getBusinessId(),
                requestDTO.getEmployeeId(),
                requestDTO.getDate()
        );
        return ResponseEntity.ok(availabilityData);
    }

    @PostMapping("/new")
    public ResponseEntity<Object> createReservation(@Valid @RequestBody ResDTO.ReservationCreationRequestDTO requestDTO) {
        logger.info("POST /api/reservations/new request received");

        String customerName = requestDTO.getCustomerName();
        String customerPhone = requestDTO.getCustomerPhone();
        String date = requestDTO.getDate();
        int businessId = requestDTO.getBusinessId();
        int serviceId = requestDTO.getServiceId();
        int employeeId = requestDTO.getEmployeeId();

        String response = resService.createReservation(customerPhone, customerName, date, businessId, serviceId, employeeId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteReservation(
            @PathVariable int id,
            @RequestParam Integer businessId) {

        logger.info("DELETE /api/reservations/{} request received", id);
        resService.deleteReservation(id, businessId);
        return ResponseEntity.ok("Deleted successfully");
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateReservation(
            @PathVariable int id,
            @Valid @RequestBody ResDTO.ReservationUpdateRequestDTO requestDTO) {

        logger.info("PUT /api/reservations/{} request received", id);
        resService.updateReservation(id, requestDTO);
        return ResponseEntity.ok("Updated successfully");
    }
}
