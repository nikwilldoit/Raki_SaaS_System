package com.raki.pos.discount;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DiscountService {

    private final DiscountRepository repo;

    public DiscountService(DiscountRepository repo) {
        this.repo = repo;
    }

    public DiscountDTO.DiscountResponse createDiscount(DiscountDTO.DiscountRequest req) {
        // default αν δεν έρθει
        if (req.getScope() == null || req.getScope().isBlank()) {
            req.setScope("PRODUCT");
        }

        Long id = repo.createPolicy(req);

        // PRODUCT ή BOTH -> products
        if ("PRODUCT".equalsIgnoreCase(req.getScope()) ||
                "BOTH".equalsIgnoreCase(req.getScope())) {
            repo.assignDiscountToProducts(id, req.getProductIds(), req.getBusinessId());
        }

        // SERVICE ή BOTH -> services
        if ("SERVICE".equalsIgnoreCase(req.getScope()) ||
                "BOTH".equalsIgnoreCase(req.getScope())) {
            repo.assignDiscountToServices(id, req.getServiceIds(), req.getBusinessId());
        }

        return repo.findAllByBusiness(req.getBusinessId())
                .stream()
                .filter(d -> d.getId().equals(id))
                .findFirst()
                .orElseThrow();
    }

    public DiscountDTO.DiscountResponse updateDiscount(Long id, DiscountDTO.DiscountRequest req) {
        if (req.getScope() == null || req.getScope().isBlank()) {
            req.setScope("PRODUCT");
        }

        repo.updatePolicy(id, req);

        if ("PRODUCT".equalsIgnoreCase(req.getScope()) ||
                "BOTH".equalsIgnoreCase(req.getScope())) {
            repo.assignDiscountToProducts(id, req.getProductIds(), req.getBusinessId());
        } else {
            // αν scope δεν περιλαμβάνει πλέον products, καθάρισε
            repo.assignDiscountToProducts(id, List.of(), req.getBusinessId());
        }

        if ("SERVICE".equalsIgnoreCase(req.getScope()) ||
                "BOTH".equalsIgnoreCase(req.getScope())) {
            repo.assignDiscountToServices(id, req.getServiceIds(), req.getBusinessId());
        } else {
            repo.assignDiscountToServices(id, List.of(), req.getBusinessId());
        }

        return repo.findAllByBusiness(req.getBusinessId())
                .stream()
                .filter(d -> d.getId().equals(id))
                .findFirst()
                .orElseThrow();
    }

    public List<DiscountDTO.DiscountResponse> listDiscounts(Long businessId) {
        return repo.findAllByBusiness(businessId);
    }

    public void deleteDiscount(Long id, Long businessId) {
        repo.deletePolicy(id, businessId);
    }
}
