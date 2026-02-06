package com.raki.pos.payment;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "http://localhost:3000")
public class PaymentController {

    private final PaymentService service;

    public PaymentController(PaymentService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<PaymentDTO.PaymentResult> createPayment(
            @RequestBody PaymentDTO.CreatePaymentRequest request) {

        PaymentDTO.PaymentResult result = service.createPayment(request);
        return ResponseEntity.ok(result);
    }
}
