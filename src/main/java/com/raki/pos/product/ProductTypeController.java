package com.raki.pos.product;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class ProductTypeController {

    private final ProductTypeRepository typeRepo;

    public ProductTypeController(ProductTypeRepository typeRepo) {
        this.typeRepo = typeRepo;
    }

    @GetMapping("/product-types")
    public ResponseEntity<List<ProductTypeDTO>> getAllProductTypes() {
        List<ProductTypeDTO> list = typeRepo.findAll();
        return ResponseEntity.ok(list);
    }
}
