package com.raki.pos.payment;

import com.raki.pos.model.Payment;
import com.raki.pos.model.PaymentSplit;
import com.raki.pos.model.PaymentSplitItem;
import com.raki.pos.order.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PaymentService {

    private final PaymentRepository repo;
    private final OrderRepository orderRepository;

    public PaymentService(PaymentRepository repo, OrderRepository orderRepository) {
        this.repo = repo;
        this.orderRepository = orderRepository;
    }

    @Transactional
    public PaymentDTO.PaymentResult createPayment(PaymentDTO.CreatePaymentRequest req) {
        Payment p = new Payment();
        p.setBusinessId(req.getBusinessId());
        p.setOrderId(req.getOrderId());
        p.setTotalAmount(req.getTotalAmount());
        p.setTotalTip(req.getTotalTip());
        p.setPaymentMethod(req.getPaymentMethod());
        p.setSplit(req.isSplit());
        p.setStatus("PENDING");

        Long paymentId = repo.insertPayment(p);

        List<Long> paidOrderItemIds = List.of();

        if (req.isSplit()) {
            if (req.getSplits() == null || req.getSplits().isEmpty()) {
                throw new IllegalArgumentException("Split payment requires splits");
            }

            paidOrderItemIds = req.getSplits().stream()
                    .flatMap(sDto -> sDto.getItems().stream())
                    .map(PaymentDTO.SplitItemDTO::getOrderItemId)
                    .distinct()
                    .collect(Collectors.toList());

            for (PaymentDTO.SplitDTO sDto : req.getSplits()) {
                PaymentSplit s = new PaymentSplit();
                s.setPaymentId(paymentId);
                s.setPayerName(sDto.getPayerName());
                s.setAmount(sDto.getAmount());
                s.setTipAmount(sDto.getTipAmount());
                s.setPaymentMethod(sDto.getPaymentMethod());
                s.setStatus("PENDING");

                Long splitId = repo.insertSplit(s);

                if (sDto.getItems() != null) {
                    for (PaymentDTO.SplitItemDTO iDto : sDto.getItems()) {
                        PaymentSplitItem psi = new PaymentSplitItem();
                        psi.setSplitId(splitId);
                        psi.setOrderItemId(iDto.getOrderItemId());
                        psi.setAmount(iDto.getAmount());
                        repo.insertSplitItem(psi);
                    }
                }
            }

            // mark only items of this split as paid
            orderRepository.markOrderItemsPaid(paidOrderItemIds);
        }
        else {
            // full payment: all UNPAID items turn to PAID state
            orderRepository.markAllItemsPaidForOrder(req.getOrderId());
        }

        // Total amount of products in the order without discounts
        BigDecimal productsTotal = orderRepository.sumOrderItemsTotal(req.getOrderId());

        //check if discount amount is provided, otherwise calculate it from percent
        BigDecimal discountAmount = req.getDiscountAmount();
        if (discountAmount == null) {
            BigDecimal discountPercent = req.getDiscountPercent() != null
                    ? req.getDiscountPercent()
                    : BigDecimal.ZERO;
            discountAmount = productsTotal
                    .multiply(discountPercent)
                    .divide(BigDecimal.valueOf(100));
        }

        // sum(products) - discount
        BigDecimal orderTotal = productsTotal.subtract(discountAmount);

        // Amount paid so far for this order (including this payment)
        BigDecimal paidTotal = repo.sumPaymentsTotalAmount(req.getOrderId());

        // check if order has been paid
        boolean completed = paidTotal.compareTo(orderTotal) >= 0;


        if (completed) {
            // 1) close the order
            orderRepository.updateStatus(req.getOrderId(), "CLOSED");

            // 2) mark all items as PAID if not already done
            orderRepository.markAllItemsPaidForOrder(req.getOrderId());

            // 3) set all payments as COMPLETED
            repo.markPaymentsCompletedForOrder(req.getOrderId());

            // 4) set splits as COMPLETED
            repo.markSplitsCompletedForOrder(req.getOrderId());
        }

        PaymentDTO.PaymentResult result = new PaymentDTO.PaymentResult();
        result.setOrderCompleted(completed);

        if (!completed) {
            result.setRemainingItems(orderRepository.findUnpaidItems(req.getOrderId()));

            // subtotal = sum of products before discount
            result.setSubtotal(productsTotal);

            result.setDiscountPercent(req.getDiscountPercent() != null ? req.getDiscountPercent() : BigDecimal.ZERO);
            result.setDiscountAmount(discountAmount != null ? discountAmount : BigDecimal.ZERO);

            // total =  (products - discount)
            result.setTotal(orderTotal);
        }
        return result;
    }
}
