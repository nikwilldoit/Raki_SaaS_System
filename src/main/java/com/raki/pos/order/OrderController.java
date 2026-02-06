// src/main/java/com/raki/pos/order/OrderController.java
package com.raki.pos.order;

import com.raki.pos.model.Order;
import com.raki.pos.model.OrderItem;
import com.raki.pos.model.Payment;
import com.raki.pos.model.Refund;
import com.raki.pos.payment.PaymentDTO;
import com.raki.pos.payment.PaymentRepository;
import com.raki.pos.refund.RefundService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "http://localhost:3000")
public class OrderController {

    private final OrderService orderService;
    private final PaymentRepository paymentRepository;
    private final RefundService refundService;
    private final AtomicLong counter = new AtomicLong(1); // demo generator

    public OrderController(OrderService orderService, PaymentRepository paymentRepository, RefundService refundService) {
        this.orderService = orderService;
        this.paymentRepository = paymentRepository;
        this.refundService = refundService;
    }

    @PostMapping
    public OrderWithItemsDTO createOrder(@RequestBody CreateOrderRequest request) {
        String orderNumber = request.getOrderNumber();
        if (orderNumber == null || orderNumber.isBlank()) {
            orderNumber = "ORD-" + counter.getAndIncrement();
        }

        Order order = new Order(
                null,
                request.getBusinessId(),
                request.getUserId(),
                null,           // discountId
                orderNumber,
                null,           // orderDate (DB default)
                "OPEN",
                request.getSpecialRequests()
        );

        List<OrderItem> items = request.getItems().stream()
                .map(line -> {
                    int quantity = line.getQuantity() != null ? line.getQuantity() : 1;
                    double unitPrice = line.getUnitPrice() != null ? line.getUnitPrice() : 0.0;
                    double totalPrice = line.getTotalPrice() != null
                            ? line.getTotalPrice()
                            : unitPrice * quantity;

                    OrderItem oi = new OrderItem(
                            null,
                            null,
                            line.getProductId(),
                            quantity,
                            "UNPAID",
                            totalPrice
                    );
                    oi.setUnitPrice(unitPrice);
                    return oi;
                })
                .toList();

        return orderService.createOrderWithItems(order, items);
    }

    // Returns order with items
    @GetMapping("/{orderId}")
    public OrderWithItemsDTO getOrderWithItems(@PathVariable Long orderId) {
        return orderService.getOrderWithItems(orderId);
    }

    // Payments for PaymentOverview.jsx
    @GetMapping("/{orderId}/payments")
    public List<PaymentDTO.PaymentSummary> getPaymentsForOrder(@PathVariable Long orderId) {
        return paymentRepository.findPaymentsForOrder(orderId);
    }

    @GetMapping("/business/{businessId}")
    public List<Order> getOrdersByBusiness(@PathVariable Long businessId) {
        return orderService.getOrdersForList(businessId);
    }

    @PutMapping("/{orderId}/refund")
    public ResponseEntity<?> refundOrder(@PathVariable Long orderId,
                                         @RequestParam(required = false) String reason) {

        Optional<Order> orderOpt = orderService.getOrderById(orderId);
        if (orderOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Order order = orderOpt.get();
        if ("REFUNDED".equals(order.getStatus())) {
            return ResponseEntity.badRequest().body("Order is already refunded");
        }

        // Σύνολο πληρωμών από payments.total_amount
        BigDecimal totalPaid = paymentRepository.sumPaymentsTotalAmount(orderId);
        if (totalPaid.compareTo(BigDecimal.ZERO) <= 0) {
            return ResponseEntity.badRequest().body("Order has no payments to refund");
        }

        Payment payment = paymentRepository.findLastCompletedByOrderId(orderId);

        // refund creation (user = null)
        Refund refund = new Refund();
        refund.setOrderId(orderId);
        refund.setPaymentId(payment != null ? payment.getPaymentId() : null);
        refund.setProcessedByUserId(null);
        refund.setAmount(totalPaid);
        refund.setReason(reason != null ? reason : "No reason provided");
        refund.setStatus("PROCESSED");
        refundService.save(refund);

        if ("CLOSED".equals(order.getStatus())) {
            order.setStatus("REFUNDED");
            orderService.save(order);
        }

        return ResponseEntity.ok("Order refunded successfully");
    }

}
