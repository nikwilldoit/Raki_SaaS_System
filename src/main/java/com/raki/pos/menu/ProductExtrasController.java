package com.raki.pos.menu;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "http://localhost:3000")
public class ProductExtrasController {

    private final ProductExtrasService productExtrasService;

    public ProductExtrasController(ProductExtrasService productExtrasService) {
        this.productExtrasService = productExtrasService;
    }

    @GetMapping("/{productId}/extras")
    public List<ProductIngredientCategoryDTO> getExtrasForProduct(
            @PathVariable Long productId) {
        return productExtrasService.getCategoriesForProduct(productId);
    }
}
