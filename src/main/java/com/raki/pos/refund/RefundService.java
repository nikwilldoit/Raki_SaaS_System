// src/main/java/com/raki/pos/refund/RefundService.java
package com.raki.pos.refund;

import com.raki.pos.model.Refund;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RefundService {

    private final RefundRepository refundRepository;

    public RefundService(RefundRepository refundRepository) {
        this.refundRepository = refundRepository;
    }

    @Transactional
    public Long save(Refund refund) {
        return refundRepository.createRefund(refund);
    }
}
