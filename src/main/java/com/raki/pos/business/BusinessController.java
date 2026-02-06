package com.raki.pos.business;

import com.raki.pos.business.model.Business;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/businesses")
@RequiredArgsConstructor
public class BusinessController {

    private final BusinessService businessService;


    @GetMapping("/{id}")
    public ResponseEntity<BusinessDTO> getBusinessById(@PathVariable Integer id) {
        return businessService.findBusinessById(id)
                .map(b -> ResponseEntity.ok(BusinessDTO.fromEntity(b)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    @GetMapping("/me")
    public ResponseEntity<BusinessDTO> getMyBusiness() {
        return businessService.findBusinessForCurrentUser()
                .map(b -> ResponseEntity.ok(
                        new BusinessDTO(
                                b.getId(),
                                b.getName(),
                                b.getAddress(),
                                b.getBusinessType(),
                                b.getPhone(),
                                b.getIsActive()
                        )))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    @PutMapping("/me")
    public ResponseEntity<BusinessDTO> updateMyBusiness(@RequestBody BusinessDTO dto) {

        Business incoming = new Business();
        incoming.setName(dto.getName());
        incoming.setAddress(dto.getAddress());
        incoming.setPhone(dto.getPhone());
        incoming.setIsActive(dto.getIsActive());

        return businessService.updateBusinessForCurrentUser(incoming)
                .map(b -> ResponseEntity.ok(BusinessDTO.fromEntity(b)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
