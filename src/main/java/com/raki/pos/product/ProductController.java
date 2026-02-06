package com.raki.pos.product;

import com.raki.pos.model.Product;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "http://localhost:3000")
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @GetMapping
    public List<Product> listByBusiness(@RequestParam Long businessId) {
        return service.getActiveProductsForBusiness(businessId);
    }

    @GetMapping("/{id}")
    public Product getOne(@PathVariable Long id) {
        return service.getProduct(id);
    }

    @PostMapping
    public Long createProduct(@RequestBody Product product) {
        return service.createOrUpdate(product);
    }

    @PutMapping("/{id}")
    public Long updateProduct(@PathVariable Long id, @RequestBody Product product) {
        product.setProductId(id);
        return service.createOrUpdate(product);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id, @RequestParam Long businessId) {
        service.delete(id, businessId);
    }
}