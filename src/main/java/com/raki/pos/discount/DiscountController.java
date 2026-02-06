package com.raki.pos.discount;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/discounts")
public class DiscountController {

    private final DiscountService discountService;

    /**
     * Constructor for DiscountController.
     *
     * @param discountService the service used to manage discounts
     */
    public DiscountController(DiscountService discountService) {
        this.discountService = discountService;
    }


    @GetMapping
    public ResponseEntity<List<DiscountDTO.DiscountResponse>> getDiscounts(
            @RequestParam("businessId") Long businessId
    ) {
        List<DiscountDTO.DiscountResponse> discounts =
                discountService.listDiscounts(businessId);
        return ResponseEntity.ok(discounts);
    }


    @PostMapping
    public ResponseEntity<DiscountDTO.DiscountResponse> createDiscount(
            @RequestBody DiscountDTO.DiscountRequest request
    ) {
        DiscountDTO.DiscountResponse created =
                discountService.createDiscount(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DiscountDTO.DiscountResponse> updateDiscount(
            @PathVariable("id") Long id,
            @RequestBody DiscountDTO.DiscountRequest request
    ) {
        DiscountDTO.DiscountResponse updated =
                discountService.updateDiscount(id, request);
        return ResponseEntity.ok(updated);
    }


    /**
     * Deletes a discount and clears the discount_id from the associated products.
     *
     * @param id         the ID of the discount to delete
     * @param businessId the ID of the business
     * @return a ResponseEntity with no content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDiscount(
            @PathVariable("id") Long id,
            @RequestParam("businessId") Long businessId
    ) {
        discountService.deleteDiscount(id, businessId);
        return ResponseEntity.noContent().build();
    }
}