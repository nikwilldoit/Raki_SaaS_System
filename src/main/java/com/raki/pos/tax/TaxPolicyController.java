package com.raki.pos.tax;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class TaxPolicyController {

    private final TaxPolicyRepository taxRepo;

    public TaxPolicyController(TaxPolicyRepository taxRepo) {
        this.taxRepo = taxRepo;
    }

    @GetMapping("/taxes")
    public ResponseEntity<List<TaxPolicyDTO>> getAllTaxes() {
        List<TaxPolicyDTO> list = taxRepo.findAll();
        return ResponseEntity.ok(list);
    }

    @PostMapping("/taxes")
    public ResponseEntity<String> addTax(@RequestBody TaxPolicyDTO tax) {
        taxRepo.insert(tax.getName(), tax.getRate(), tax.getTaxType());
        return ResponseEntity.ok("Saved");
    }
}
